$(document).ready(async function () {
    "use strict";
    $.ajax("/rest/user", {
        dataType: "json",
        success: function (response) {
            const user = response;
            const roles = user.roles
            let roleName = '';
            roles.forEach(function (el) {
                roleName += el['beautifulName'] + ' ';
            })
            let tableFilling = `$(
                            <td>${user.id}</td>
                            <td>${user.username}</td>
                            <td>${user.age}</td>
                            <td>${user.position}</td>
                            <td>${roleName}</td>
                )`;
            $('#userName').text(user.username);
            $('#userRoles').text(roleName);
            $('.table-secondary').append(tableFilling);
        }
    });
});