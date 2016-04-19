namespace jmxhealth {
    var pubsub: pubsub.PubSub = require('pubsub-js'),
        gui = require('nw.gui'),
        currentWindow = gui.Window.get();

    class DetailListController {
        static $injects = ['$scope'];

        public failedStates: DetailStateResponse[];
        private visible: boolean;
        // private filterState: api.StateResponse;

        constructor($scope: angular.IScope) {
            this.visible = false;

            currentWindow.on('close', (event: any) => {
                currentWindow.hide();
                this.visible = false;
            });

            pubsub.subscribe(topic.OVERALL_STATE, (message, state) => {
                if (this.shouldNotify()) {
                    this.notify(state);
                }
            });

            pubsub.subscribe(topic.FAILED_STATES, (message, failed) => {
                this.failedStates = this.prepareStates(failed);
                $scope.$apply();
            });

            pubsub.subscribe(topic.SHOW_DETAIL, (message, state) => {
                // this.filterState = state;
                $scope.$apply();
                this.show();
            });

            pubsub.subscribe(topic.PAUSE, () => {
                $scope.$apply();
            });

            pubsub.subscribe(topic.RESUME, () => {
                $scope.$apply();
            });
        }

        public show(): void {
            currentWindow.show();
            currentWindow.focus();
            this.visible = true;
        }

        public shouldNotify(): boolean {
            if (!this.failedStates || this.failedStates.length === 0) {
                return false;
            }

            for (let state of this.failedStates) {
                if (!state.paused) {
                    return true;
                }
            }

            return false;
        }

        public pauseState(state: api.StateResponse): void {
            state.paused = true;

            pubsub.publish(topic.PAUSE, state);
        }

        public resumeState(state: api.StateResponse): void {
            state.paused = false;

            pubsub.publish(topic.RESUME, state);
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

        private notify(state: string): void {
            var promise: angular.IPromise<void>;

            if (state === 'WARN') {
                promise = Notify.warn('Watch out. It seems the something is not running as expected. Check the details before it get\'s really bad.');
            }
            else if (state === 'ALERT') {
                promise = Notify.error('Uh Oh! Something went terribly wrong. Check the details before everything get\'s even worse.');
            }

            promise.then(() => {
                this.show();
            });
        }

        private prepareStates(states: api.StateResponse[]): DetailStateResponse[] {
            var prepared: DetailStateResponse[] = [];

            for (var state of states) {
                var preparedState: DetailStateResponse = {
                    application: state.application,
                    environment: state.environment,
                    server: state.server,
                    overallState: state.overallState,
                    unsuccessfulAttributes: [],
                    paused: state.paused
                };



                if (state.unsuccessfulAttributes) {
                    for (var attribute of state.unsuccessfulAttributes) {
                        preparedState.unsuccessfulAttributes.push({
                            attributeName: attribute.attributeName,
                            state: attribute.state,
                            message: attribute.message,
                            timestamp: attribute.timestamp,
                            lines: this.messageToLines(attribute.message)
                        });
                    }
                }

                prepared.push(preparedState);
            }

            return prepared;
        }

        private messageToLines(message: string): string[] {
            if (!message) {
                return null;
            }

            return message.split('\n');
        }
    }

    angular.module('jmxhealth.detail', [])
        .component('detailList', {
            controller: DetailListController,
            templateUrl: './target/templates/detail-list.html'
        });
}
