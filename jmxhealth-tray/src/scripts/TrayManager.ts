namespace jmxhealth {
    // Load native UI library
    var gui = require('nw.gui'),
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

            this.setupContextMenu();
            this.initPopupWindow();
        }

        private showPopup(): void {
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
                toolbar: true,
                frame: true,
                resizable: false,
                show: false,
                show_in_taskbar: true,
                width: popupWidth,
                height: popupHeight
            };

            this.popup = gui.Window.open('./target/templates/tray-popup.html', params);

            this.popup.on('close', (event: any) => {
                this.popup.hide();
            });

            // this.popup.on('blur', (event: any) => {
            //     this.popup.hide();
            // });
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
