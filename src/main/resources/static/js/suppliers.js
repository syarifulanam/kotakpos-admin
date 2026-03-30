document.addEventListener('DOMContentLoaded', function () {
    var deleteModal = document.getElementById('deleteModal');
    if (deleteModal) {
        deleteModal.addEventListener('show.bs.modal', function (event) {
            var button = event.relatedTarget;
            var supplierId = button.getAttribute('data-id');
            var supplierName = button.getAttribute('data-name');
            var modalBodyInput =deleteModal.querySelector('.modal-body #supplierName');
            var deleteForm = deleteModal.querySelector('#deleteForm');

            modalBodyInput.textContent =supplierName;
            deleteForm.action = '/suppliers/delete' + supplierId;
        });
    }
});