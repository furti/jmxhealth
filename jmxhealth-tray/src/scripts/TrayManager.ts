namespace jmxhealth {
    // Load native UI library
    var gui = require('nw.gui'),
        pubsub = require('pubsub-js'),
        currentWindow = gui.Window.get(),
        popupWidth = 200,
        popupHeight = 400;

    export class TrayManager {
        private initialized: boolean = false;
        private tray: any;
        private popup: any;

        constructor() {

        }

        public initialize() {
            if (this.initialized) {
                return;
            }

            this.tray = new gui.Tray({ title: 'Jmxhealth', icon: 'icons/network_check.png' });
            this.tray.on('click', () => this.showPopup());

            pubsub.subscribe(OVERALL_STATE, (msg, data) => {
                var icon;

                if (data === 'ALERT') {
                    icon = 'icons/error.png';
                }
                else if (data === 'WARN') {
                    icon = 'icons/warning.png';
                }
                else if (data === 'OK') {
                    icon = 'icons/network_check.png';
                }

                this.tray.icon = icon;
            });

            this.setupContextMenu();
            this.initPopupWindow();
        }

        private showPopup(): void {
            if (!this.popup) {
                return;
            }

            this.popup.moveTo(window.screen.availWidth - popupWidth - 100, window.screen.availHeight - popupHeight - 50);
            this.popup.show();
            this.popup.focus();
        }

        private setupContextMenu(): void {
            var menu = new gui.Menu();
            var closeItem = new gui.MenuItem({ type: 'normal', label: 'Close' });
            closeItem.on('click', () => this.close());

            menu.append(closeItem);
            this.tray.menu = menu;
        }

        private close(): void {
            this.popup.close(true);
            currentWindow.close();
        }

        private initPopupWindow(): void {
            // create window
            var params = {
                frame: true,
                resizable: false,
                show: false,
                // showInTaskbar: true,
                width: popupWidth,
                height: popupHeight,
                id: 'popup'
            };

            gui.Window.open('./tray-popup.html', params, (win) => {
                this.popup = win;
                this.popup.on('close', (event: any) => {
                    this.popup.hide();
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
