namespace jmxhealth {
    // Load native UI library
    var gui = require('nw.gui'),
        window = gui.Window.get(),
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
            this.tray.on('click', (event) => this.showPopup(event));

            this.setupContextMenu();
            this.initPopupWindow();
        }

        private showPopup(event: any): void {
            this.popup.moveTo(event.x - popupWidth, event.y - popupHeight - 10);
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
            window.close();
        }

        private initPopupWindow(): void {
            // create window
            var params = {
                toolbar: true,
                frame: false,
                transparent: false,
                resizable: false,
                show: false,
                show_in_taskbar: false,
                width: popupWidth,
                height: popupHeight
            };

            this.popup = gui.Window.open('./target/templates/tray-popup.html', params);

            this.popup.on('close', (event: any) => {
                this.popup.hide();
            });

            this.popup.on('blur', (event: any) => {
                this.popup.hide();
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
