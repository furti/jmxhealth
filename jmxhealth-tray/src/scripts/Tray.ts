namespace jmxhealth {
    // Load native UI library
    var gui = require('nw.gui'),
        window = gui.Window.get();

    export class TrayManager {
        private initialized: boolean = false;
        private tray: any;

        constructor() {

        }

        public initialize() {
            if (this.initialized) {
                return;
            }

            this.tray = new gui.Tray({ title: 'Jmxhealth', icon: 'icons/network_check.png' });

            this.tray.on('click', (event) => this.handleClick(event));

            var menu = new gui.Menu();
            var closeItem = new gui.MenuItem({ type: 'normal', label: 'Close' });
            closeItem.on('click', (event) => window.close());

            menu.append(closeItem);
            this.tray.menu = menu;
        }

        private handleClick(event: any): void {
            console.log(event);
        }
    }

    angular.module('jmxhealth.tray', [])
        .run(['trayManager', function(trayManager: TrayManager) {
            trayManager.initialize();
        }])
        .factory('trayManager', [function() {
            return new TrayManager();
        }]);

    // Give it a menu
    // var menu = new gui.Menu();
    // menu.append(new gui.MenuItem({ type: 'checkbox', label: 'box1' }));
    // tray.menu = menu;
    //
    // // Remove the tray
    // tray.remove();
    // tray = null;
}
