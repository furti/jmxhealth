namespace jmxhealth {
    // Load native UI library
    var gui = require('nw.gui'),
        pubsub = require('pubsub-js'),
        currentWindow = gui.Window.get(),
        popupWidth = 400,
        popupHeight = 600;

    export class TrayManager {
        private initialized: boolean = false;
        private tray: any;
        private popup: any;
        private popupShown: boolean;

        constructor() {

        }

        public initialize() {
            if (this.initialized) {
                return;
            }

            this.tray = new gui.Tray({ title: 'Jmxhealth', icon: 'icons/network_check.png' });
            this.tray.on('click', () => this.togglePopup());

            pubsub.subscribe(topic.OVERALL_STATE, (msg, data) => {
                this.tray.icon = HealthUtils.stateIconPath(data);
            });

            pubsub.subscribe(topic.NO_SERVERS, () => {
                this.tray.icon = 'icons/warning.png';
            });

            this.setupContextMenu();
            this.initPopupWindow();
        }

        private togglePopup(): void {
            if (!this.popup) {
                return;
            }

            if (this.popupShown) {
                this.popupShown = false;
                this.popup.hide();
            }
            else {
                this.popup.moveTo(window.screen.availWidth - popupWidth - 100, window.screen.availHeight - popupHeight - 50);
                this.popup.show();
                this.popup.focus();
                this.popupShown = true;
            }
        }

        private setupContextMenu(): void {
            var menu = new gui.Menu();

            var creditsItem = new gui.MenuItem({ type: 'normal', label: 'Credits' });
            creditsItem.on('click', () => this.showCredits());
            menu.append(creditsItem);

            var closeItem = new gui.MenuItem({ type: 'normal', label: 'Close' });
            closeItem.on('click', () => this.close());
            menu.append(closeItem);

            this.tray.menu = menu;
        }

        private close(): void {
            this.popup.close(true);
            currentWindow.close(true);
        }

        private showCredits(): void {
            gui.Window.open('./health-credits.html', {
                frame: true,
                resizable: false,
                width: 300,
                height: 300
            });
        }

        private initPopupWindow(): void {
            // create window
            var params = {
                frame: true,
                resizable: false,
                show: false,
                width: popupWidth,
                height: popupHeight
            };

            this.popupShown = false;

            gui.Window.open('./tray-popup.html', params, (win) => {
                this.popup = win;
                this.popup.on('close', (event: any) => {
                    this.popup.hide();
                    this.popupShown = false;
                });

                this.popup.on('minimize', () => {
                    this.popupShown = false;
                })

                win.on('loaded', () => {
                    pubsub.publish(topic.INITIALIZED, 'TrayManager');
                });
            });
        }
    }

    angular.module('jmxhealth.tray', [])
        .run(['trayManager', function(trayManager: TrayManager) {
            trayManager.initialize();
        }])
        .factory('trayManager', [function() {
            return new TrayManager();
        }]);
}
