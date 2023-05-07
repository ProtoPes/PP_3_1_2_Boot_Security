$(document).ready(async function () {
    let users;
    let allRoles;
    let roleUser;
    let tableBody;
    let admin;
    $.ajax('/rest/users', {
        dataType: 'json',
        success: async function (response) {
            users = response['users'];
            allRoles = response['roles'];
            admin = response['admin'][0];
            $('#adminName').text(admin.username);
            $('#adminRoles').text(getRoles(admin.roles));
            roleUser = allRoles.find(item => item['beautifulName'] === 'User');
            tableBody = $('#bodyUsersTable');
            users.forEach(function (user) {
                let tableFilling = fillCell(user);
                tableBody.append(tableFilling);
            });

            await addOpenModalEventListener($('.btn-info'), 'formEdit', 'toggleModalEdit');
            await addOpenModalEventListener($('.buttonDelete'), 'formDelete', 'toggleModalDelete');

            await addConfirmNewUserEventListener();
            await addConfirmDeleteEventListener();
            await addConfirmEditEventListener();

            let selectRoleNewUser = $('#rolesNew');
            await addRolesToSelect(selectRoleNewUser, [])
        }
    });

    $('.modal').on('hidden.bs.modal', function () {
        $(this).find('option').remove();
        $(this).find('form')[0].reset();
    });

    async function addConfirmEditEventListener() {
        $('#confirmModalEdit').on("click", async function (event) {
            event.preventDefault();
            const form = document.getElementById('formEdit');
            if (form.checkValidity() === false) {
                form.reportValidity();
            } else {
                const userFromForm = await getFormData(form);
                const userId = userFromForm.id;
                const init = await createInitForFetch(userFromForm, 'POST');
                await fetch('rest', init)
                    .then(async response => {
                        if (!response.ok) {
                            const errorJson = await response.json();
                            throw new Error(errorJson['errorMessage']);
                        }
                        return await response.json();
                    })
                    .then(async () => {
                        const tableFilling = fillCell(userFromForm);
                        $('#' + userId).replaceWith(tableFilling);
                        let index = users.findIndex(user => user.id == userId);
                        users[index] = userFromForm;
                        $('#toggleModalEdit').modal('hide');
                        await addOpenModalEventListener($('#editUser_' + userId), 'formEdit', 'toggleModalEdit');
                        await addOpenModalEventListener($('#deleteUser_' + userId), 'formDelete', 'toggleModalDelete');
                    })
                    .catch(error => {
                        alert(error);
                    });
            }
        });
    }

    async function addConfirmNewUserEventListener() {
        $('#confirmNew').on("click", async function (event) {
            event.preventDefault();
            const form = document.getElementById('formNew');
            if (form.checkValidity() === false) {
                form.reportValidity();
            } else {
                const userFromForm = await getFormData(form);
                const init = await createInitForFetch(userFromForm, 'POST');
                await fetch('/rest', init)
                    .then(async response => {
                        if (!response.ok) {
                            const errorJson = await response.json();
                            throw new Error(errorJson['errorMessage']);
                        }
                        return await response.json();
                    })
                    .then(async jsonResponse => {
                        let tableFilling = fillCell(jsonResponse);
                        tableBody.append(tableFilling);
                        users.push(jsonResponse);
                        const userId = jsonResponse.id;
                        let newUserTabLink = $('#users-tab');
                        newUserTabLink.tab('show');
                        $("#formNew")[0].reset();
                        await addOpenModalEventListener($('#editUser_' + userId), 'formEdit', 'toggleModalEdit');
                        await addOpenModalEventListener($('#deleteUser_' + userId), 'formDelete', 'toggleModalDelete');
                    })
                    .catch(error => {
                        alert(error);
                    });
            }
        });
    }

    async function addConfirmDeleteEventListener() {
        $('#confirmModalDelete').on("click", async function (event) {
            event.preventDefault();
            const form = document.getElementById('formDelete');
            const userFromForm = await getFormData(form);
            const userId = userFromForm.id;
            const init = await createInitForFetch(userFromForm, 'DELETE');
            await fetch(`/rest/${userId}`, init)
                .then(async response => {
                    if (!response.ok) {
                        const errorJson = await response.json();
                        throw new Error(errorJson['errorMessage']);
                    }
                    return await response.json();
                })
                .then(async () => {
                    $('#' + userId).remove();
                    let index = users.findIndex(user => user.id == userId);
                    users.splice(index, 1);
                    $('#toggleModalDelete').modal('hide');
                })
                .catch(error => {
                    alert(error);
                });
        });
    }

    async function createInitForFetch(userFromForm, method) {
        const init = {method: method};
        if (method !== 'DELETE') {
            const userInJson = JSON.stringify(userFromForm);
            init['headers'] = {'Content-Type': 'application/json'};
            init['body'] = userInJson;
        }
        return init;
    }


    async function getFormData(form) {
        const formData = new FormData(form);
        const data = {};
        for (const [name, value] of formData.entries()) {
            if (name !== 'roles') {
                data[name] = value;
            } else {
                data[name] = [roleUser,]
                if (allRoles[value]['beautifulName'] !== 'User') {
                    data[name].push(allRoles[value]);
                }
            }
        }
        return data;
    }

    async function addOpenModalEventListener(targetButton, formId, modalId) {
        targetButton.on("click", async function (event) {
            event.preventDefault();
            let attr = $(this).attr('id');
            let id = attr.split('_').at(-1);
            const user = users.find(item => item['id'] == id);
            const roles = user.roles;
            $('#' + modalId).modal('show');
            $('#' + formId + ' input[name="id"]').val(user.id);
            $('#' + formId + ' input[name="username"]').val(user.username);
            $('#' + formId + ' input[name="age"]').val(user.age);
            $('#' + formId + ' input[name="position"]').val(user.position);
            let rolesSelect = $('#' + formId + ' select');
            await addRolesToSelect(rolesSelect, roles);
        });
    }

    async function addRolesToSelect(targetSelect, userRoles) {
        targetSelect.attr('size', allRoles.length);
        allRoles.forEach(function (item, index) {
            let name = item['beautifulName'];
            let option = $("<option/>", {
                html: name,
                value: index,
            });
            if ((userRoles.length === 1 && name === 'User') || (userRoles.length > 1 && name !== 'User')) {
                option.attr("selected", "selected");
            }
            targetSelect.append(option);
        });
    }


    function getRoles(roles) {
        let roleName = '';
        roles.forEach(function (role) {
            roleName += role['beautifulName'] + ' ';
        })
        return roleName;
    }

    function fillCell(entity) {
        const roleName = getRoles(entity.roles);
        const id = entity.id;
        return `$(
                            <tr id="${id}">
                            <td>${id}</td>
                            <td>${entity.username}</td>
                            <td>${entity.age}</td>
                            <td>${entity.position}</td>
                            <td>${roleName}</td>
                            <td>
                            <button
                              type="button" class="btn btn-sm btn-info" id="${'editUser_' + id}">Edit</button>
                            </td>
                            <td><button
                              type="button" class="btn btn-danger btn-sm buttonDelete" id="${'deleteUser_' + id}">Delete</button></td>
                            </tr>
                )`;
    }
});
