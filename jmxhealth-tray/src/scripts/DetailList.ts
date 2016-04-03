namespace jmxhealth {
    var pubsub: pubsub.PubSub = require('pubsub-js'),
        gui = require('nw.gui'),
        currentWindow = gui.Window.get();

    class DetailListController {
        static $injects = ['$scope'];

        public failedStates: api.StateResponse[];
        private filterState: api.StateResponse;

        constructor($scope: angular.IScope) {
            pubsub.subscribe(topic.FAILED_STATES, (message, failed) => {
                this.failedStates = failed;

                if (this.shouldShow()) {
                    this.show();
                }

                $scope.$apply();
            });

            pubsub.subscribe(topic.SHOW_DETAIL, (message, state) => {
                this.filterState = state;
                $scope.$apply();
                this.show();
            });
        }

        public show(): void {
            currentWindow.show();
            currentWindow.focus();
        }

        public shouldShow(): boolean {
            if (!this.failedStates || this.failedStates.length === 0) {
                return false;
            }

            //TODO: we have to check if a failed application is paused. -> don't popup in that case

            return true;
        }

        public stateIcon(state: api.StateResponse): string {
            return HealthUtils.stateIcon(state.overallState);
        }

        public stateIconClass(state: api.StateResponse): string {
            return HealthUtils.stateIconClass(state.overallState);
        }

        public attributeIcon(attribute: api.AttributeState): string {
            return HealthUtils.stateIconPath(attribute.state);
        }

        public inFailedState(): boolean {
            if (!this.failedStates || this.failedStates.length === 0) {
                return false;
            }

            return true;
        }
    }

    angular.module('jmxhealth.detail', [])
        .component('detailList', {
            controller: DetailListController,
            templateUrl: './target/templates/detail-list.html'
        });
}
