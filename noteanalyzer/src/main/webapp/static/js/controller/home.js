var noteApp = angular.module('NoteApp');
noteApp.controller('HomeCtrl', function($scope, $stateParams, $state, $auth, $http, $uibModal, toastr, $rootScope, noteUploadAPI, fileUpload, NoteService) {
	$scope.noteAnalyzed = function() {
		NoteService.noteAnalyze($scope.zipCode);
	};


	if ($stateParams.loginState === 'inputNoteForm') {
		NoteService.createNote($rootScope.submitInputFormModel).then(function() {
			$rootScope.submitInputFormModel = {};
			$state.go('noteDashboard');
		}, function(errResponse) {
			console.error('Error while creating NOTE');
		});
	}

	$scope.uploadFile = function() {
		if ($auth.isAuthenticated()) {
			fileUpload.uploadFileToUrl($scope.myFile, noteUploadAPI);
		} else {
			$rootScope.noteUploadFile = $scope.myFile;
			$state.go('login', {
				'referer' : 'home',
				'loginState' : 'noteFileUpload'
			});
		}
	};

	if ($stateParams.loginState === 'noteFileUpload') {
		if ($rootScope.noteUploadFile) {
			fileUpload.uploadFileToUrl($rootScope.noteUploadFile, noteUploadAPI);
			$rootScope.noteUploadFile = undefined;
			$state.go('noteDashboard');
		}
	}


});

noteApp.controller('noteInputFormController', function($scope, $rootScope, $state, $uibModalInstance, noteInputFormModel, $auth, NoteService) {
	$scope.noteInputFormModel = noteInputFormModel;
	$scope.hasError = function(field, validation) {
		if (validation) {
			return ($scope.noteInputForm[field].$dirty && $scope.noteInputForm[field].$error[validation]) || ($scope.submitted && $scope.noteInputForm[field].$error[validation]);
		}
		return ($scope.noteInputForm[field].$dirty && $scope.noteInputForm[field].$invalid) || ($scope.submitted && $scope.noteInputForm[field].$invalid);
	};

	$scope.dateOptions = {
		/*dateDisabled: disabled,*/
		formatYear : 'yy',
		maxDate : new Date(2100, 12, 31),
		minDate : new Date(1800, 12, 31),
		startingDay : 1
	};

	$scope.noteDate = {
		opened : false
	};
	$scope.noteDate = function() {
		$scope.noteDate.opened = true;
	};
	$scope.altInputFormats = ['MM/dd/yyyy'];
	$scope.save = function() {
		$scope.submitted = true;
		if ($scope.noteInputForm.$valid) {

			if ($auth.isAuthenticated()) {
				$uibModalInstance.close();
				createNoteService();

			} else {
				$uibModalInstance.close($scope.noteInputFormModel);
				$state.go('login', {
					'referer' : 'home',
					'loginState' : 'inputNoteForm'
				});
			}
		};
	};
	$scope.cancel = function() {
		$uibModalInstance.dismiss('cancel');
	};

	function createNoteService() {
		NoteService.createNote($scope.noteInputFormModel).then(function() {
			$state.go('noteDashboard');
		}, function(errResponse) {
			console.error('Error while creating NOTE');
		});
	}


});


noteApp.constant('noteUploadAPI', 'api/noteUpload');

noteApp.directive('fileModel', ['$parse', function($parse) {
	return {
		restrict : 'A',
		link : function(scope, element, attrs) {
			var model = $parse(attrs.fileModel);
			var modelSetter = model.assign;

			element.bind('change', function() {
				scope.$apply(function() {
					modelSetter(scope, element[0].files[0]);
				});
			});
		}
	};
}]);

noteApp.service('fileUpload', ['$http', 'toastr', function($http, toastr) {
	this.uploadFileToUrl = function(file, uploadUrl) {
		var fd = new FormData();
		fd.append('file', file);

		$http.post(uploadUrl, fd, {
			transformRequest : angular.identity,
			headers : {
				'Content-Type' : undefined
			}
		}).then(function(response) {
			toastr.success('File has been uploaded Successfully!!');
		}, function(response) {
			toastr.error('Unable to upload file. Please try after sometime.');
		});
	}
}]);

noteApp.factory('NoteService', ['$http', 'toastr', '$q', '$rootScope', '$uibModal', function($http, toastr, $q, $rootScope, $uibModal) {
	var factory = {
		createNote : createNote,
		noteAnalyze : noteAnalyze,
		getNoteDetail : getNoteDetail,
		deleteNote : deleteNote,
		editNote : editNote
	};

	return factory;

	function createNote(noteInputFormModel) {
		var deferred = $q.defer();
		$http.post('analyzeNote/createNote', noteInputFormModel)
			.then(
				function(response) {
					deferred.resolve(response.data);
				},
				function(errResponse) {
					console.error('Error while creating note');
					deferred.reject(errResponse);
				}
		);
		return deferred.promise;
	}
	
	function editNote(noteDetailModel) {
		var deferred = $q.defer();
		$http.post('editNote', noteDetailModel)
			.then(
				function(response) {
					deferred.resolve(response.data);
				},
				function(errResponse) {
					console.error('Error while edit note');
					deferred.reject(errResponse);
				}
		);
		return deferred.promise;
	}
	
	function deleteNote(noteId) {
		var deferred = $q.defer();
		$http.delete('deleteNote/'+noteId)
			.then(
				function(response) {
					deferred.resolve(response.data);
				},
				function(errResponse) {
					console.error('Error while delete note '+noteId);
					deferred.reject(errResponse);
				}
		);
		return deferred.promise;
	}
	
	function getNoteDetail(noteId) {
		var deferred = $q.defer();
		$http.get('getNoteDetail/'+noteId)
			.then(
				function(response) {
					deferred.resolve(response.data);
				},
				function(errResponse) {
					console.error('Error while fetching note '+noteId);
					deferred.reject(errResponse);
				}
		);
		return deferred.promise;
	}
	
	function noteAnalyze(zipCode) {
		$http.get('analyzeNote/' + zipCode).then(function(response) {

			var noteInputFormModel = response.data;
			var modalInstance = $uibModal.open({
				templateUrl : 'static/template/note-form.html',
				controller : 'noteInputFormController',
				resolve : {
					'noteInputFormModel' : noteInputFormModel
				}
			});
			modalInstance.result.then(function(response) {
				$rootScope.submitInputFormModel = response;
			}, function() {
				console.log('Modal dismissed at: ' + new Date());
			});
		}, function(response) {
			toastr.error('Unable to process your request');
		});
	}

}]);


noteApp.controller('NavbarCtrl', function($scope, $auth) {
	$scope.isAuthenticated = function() {
		return $auth.isAuthenticated();
	};
});