document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('sidebarCollapse').addEvenListener('click', function () {
        document.getElementById('sidebar').classList.toggle('active');
    if (document.getElementById('sidebar').style.marginLeft == '-250px') {
        document.getElementById('sidebar').style.marginLeft = '0';
    } else {
        document.getElementById('sidebar').style.marginLeft = '-250px';
    }
  });
});