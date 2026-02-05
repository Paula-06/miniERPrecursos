console.log('JS conectado');

let isLoggedIn = false;

const alertDiv = document.getElementById('alert');  
const buttons = document.querySelectorAll('.sidebar button');
const views = document.querySelectorAll('.view');

// UI HELPERS

//Muestra una vista y oculta las demás.

function showView(viewName) {
  views.forEach(view => {
    view.classList.toggle(
      'active',
      view.dataset.view === viewName
    );
  });
  clearAlert();
}

//Crear funcion para alertas

function showAlert(message, type = 'error') {
    // alertDiv.classList.remove('error', 'success');
    // alertDiv.classList.add(type);

  alertDiv.innerText = message;
  alertDiv.className = type;
}

// crear funcion para que el error se borre
function clearAlert() {
  alertDiv.innerText = '';
  alertDiv.className = '';
}

// =============================== API HELPERS

//GET -- Obtiene los datos / Pide información al servidor --

async function apiGet(url) {
  const response = await fetch(url, {
    credentials: 'include'
  });

  if (!response.ok) {
    const msg = await response.text();
    throw new Error(msg || 'Error en GET');
  }

  return response.json();
}

// POST -- Envía información para crear algo nuevo --

// async function apiPost(url, data) {
//   const response = await fetch(url, {
//     method: 'POST',
//     headers: { 'Content-Type': 'application/json' },
//     credentials: 'include',
//     body: JSON.stringify(data)
//   });

//   if (!response.ok) {
//     const msg = await response.text();
//     throw new Error(msg || 'Error en POST');
//   }

//   return response.json();
// }

async function apiPost(url, data) {
  if (url === '/api/auth/login') {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        data.username === 'admin' && data.password === 'admin123'
          ? resolve({ success: true })
          : reject(new Error('Usuario o contraseña incorrectos'));
      }, 500);
    });
  }

  console.log('Mock POST:', url, data);
  return { success: true };
}

// PUT -- Reemplaza o actualiza un recurso existente (normalmente completo) --

async function apiPut(url, data) {
  const response = await fetch(url, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify(data)
  });

  if (!response.ok) {
    const msg = await response.text();
    throw new Error(msg || 'Error en PUT');
  }

  return response.json();
}

// DELETE -- Borra un recurso del servidor --

async function apiDelete(url) {
  const response = await fetch(url, {
    method: 'DELETE',
    credentials: 'include'
  });

  if (!response.ok) {
    const msg = await response.text();
    throw new Error(msg || 'Error al eliminar');
  }

  return true;
}

// MENU NAVIGATION -- Lateral --

let currentView = 'loginView';

buttons.forEach(button => {
  button.addEventListener('click', () => {
    const view = button.dataset.view;

// -- Si no inicias sesion no puedes hacer nada --
    if (!isLoggedIn && view !== 'loginView') {
      showAlert('Debes iniciar sesión primero');
      showView('loginView');
      setActiveButton(
        document.querySelector('[data-view="loginView"]')
      );
      currentView = 'loginView';
      return;
    }   

    if (view === currentView) return;

    showView(view);
    setActiveButton(button);
    currentView = view;

    // -- Si estas en alguna de estas vistas ves: --
    if (view === 'dashboardView') loadDashboard();
    if (view === 'employeesView') loadEmployees();
    if (view === 'assetsView') loadAssets();
    if (view === 'assignmentsView') renderActivosDispo();
  });
});

function setActiveButton(activeBtn) {
  buttons.forEach(btn => btn.classList.remove('active'));
  activeBtn.classList.add('active');
}

// ==================================================== LOGIN

document.getElementById('loginBtn').addEventListener('click', function(event) {
  event.preventDefault();
  login();
});

// -- Funcion para el login --
async function login() {
  const username = document.getElementById('username').value.trim();
  const password = document.getElementById('password').value.trim();

  // -- Validación básica --
  if (!username || !password) {
    showAlert('Usuario y contraseña obligatorios');
    return;
  }

  try {
    await apiPost('/api/auth/login', { username, password });

    clearAlert();

    // -- Marcar LOGIN --
    isLoggedIn = true;

    // -- Cambiar vista --
    showView('dashboardView');

    // -- Marcar botón activo --
    setActiveButton(
      document.querySelector('[data-view="dashboardView"]')
    );

    // -- Marca error de Login --
  } catch (error) {
    console.error(error);
    showAlert(error.message || 'Usuario o contraseña incorrectos');
  }
}

// ======================================= DASHBOARD (placeholder)

//Variables
const cardEmployees = document.getElementById('cardEmployees');
const cardAssets = document.getElementById('cardAssets');
const cardAssignments = document.getElementById('cardAssignments');
const cardAvailable = document.getElementById('cardActiveEmployees');
const assetStateFilter = document.getElementById('assetStateFilter');

// -- Funcionalidad de los botones para ir donde quieras --
    if (cardEmployees)
    cardEmployees.onclick = () =>
        document.querySelector('[data-view="employeesView"]').click();

    if (cardAssets)
    cardAssets.onclick = () =>
        document.querySelector('[data-view="assetsView"]').click();

    if (cardAssignments)
    cardAssignments.onclick = () =>
        document.querySelector('[data-view="assignmentsView"]').click();

    if (cardAvailable)
    cardAvailable.onclick = () => {
        document.querySelector('[data-view="assetsView"]').click();
        assetStateFilter.value = 'available';
        assetStateFilter.dispatchEvent(new Event('change'));
    };

// -- Función asíncrona para cargar el dashboard --

async function loadDashboard() {
    clearAlert();

    try {
        // -- Hace un GET al backend para traer estadísticas --
        const stats = await apiGet('/api/stats');
        // --Muestra los datos recibidos en las tarjetas del dashboard --
        cardEmployees.innerText = `Empleados: ${stats.employees}`;
        cardAssets.innerText = `Activos: ${stats.assets}`;
        cardAssignments.innerText = `Asignados: ${stats.assignedAssets}`;
        // -- Dibuja el gráfico con esos datos --
        renderDashboardChart();

    } catch (error) {
        console.error(error);
        showAlert('Datos no disponibles hasta conexión con el servidor', 'info');
    }
}

//================================================= Grafica

// -- dibuja o actualiza el gráfico del dashboard sin errores ni duplicados --
// let dashboardChart = null;

// function renderDashboardChart(stats = {}) {
//   const ctx = document.getElementById('myChart');
//   if (!ctx) return;

//   if (dashboardChart) dashboardChart.destroy();

//   const safeStats = {
//     employees: stats.employees ?? 0,
//     assets: stats.assets ?? 0,
//     assignedAssets: stats.assignedAssets ?? 0,
//     availableAssets: stats.availableAssets ?? 0
//   };

//   dashboardChart = new Chart(ctx, {
//     type: 'pie',
//     data: {
//       labels: ['Empleados', 'Activos', 'Asignaciones', 'Disponibles'],
//       datasets: [{
//         label: 'Totales',
//         data: [
//           safeStats.employees,
//           safeStats.assets,
//           safeStats.assignedAssets,
//           safeStats.availableAssets
//         ],
//         backgroundColor: [
//             '#4CAF50', // Empleados
//             '#2196F3', // Activos
//             '#FFC107', // Asignaciones
//             '#c761c9'  // Disponibles
//         ],
//       }]
//     }
//   });
//}



// ========================================= EMPLEADOS (placeholder)
 // Formulario y campos
    const employeeForm = document.getElementById('employeeForm');
    const employeeFormTitle = document.getElementById('employeeFormTitle');

    const employeeName = document.getElementById('employeeName');
    const employeeSurname = document.getElementById('employeeSurname');
    const employeeEmail = document.getElementById('employeeEmail');
    const employeeDepartment = document.getElementById('employeeDepartment');
    const employeeActive = document.getElementById('employeeActive');

    const employeesTableBody = document.querySelector('#employeesTable tbody');
    const employeeSearchInput = document.getElementById('employeeSearch');
    const searchEmployeeBtn = document.getElementById('searchEmployeeBtn');
    const cancelEmployeeBtn = document.getElementById('cancelEmployeeBtn');

    let employees = [];
    let editingEmployeeId = null;

    // -- Función asíncrona para cargar los empleados --
    async function loadEmployees() {
    clearAlert();

    try {
        employees = await apiGet('/api/employees'); // Trae datos reales del backend
        renderEmployees(employees);
    } catch (error) {
        console.error(error);
        showAlert('Error cargando empleados');
    }
}

// -- Limpia la tabla de empleados --
function renderEmployees(list) {
    employeesTableBody.innerHTML = '';///"  "
    // -- Recorre la lista y crea una fila por empleado con sus datos --
    // -- Añade botones para editar y eliminar cada uno --
    list.forEach(emp => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${emp.name} ${emp.surname}</td>
            <td>${emp.email}</td>
            <td>${emp.department}</td>
            <td>${emp.active ? 'Sí' : 'No'}</td>
            <td>     
                <button onclick="editEmployee(${emp.id})">Editar</button>
                <button onclick="deleteEmployee(${emp.id})">Eliminar</button>
            </td>
        `;
        employeesTableBody.appendChild(tr);
    });
}

//BUSCAR -- filtra y muestra empleados según el texto buscado --
searchEmployeeBtn.addEventListener('click', () => {
    const text = employeeSearchInput.value.toLowerCase();

    const filtered = employees.filter(emp =>
        emp.name.toLowerCase().includes(text) ||
        emp.email.toLowerCase().includes(text)
    );

    renderEmployees(filtered);
});

//EDITAR -- prepara el formulario para editar un empleado existente --
function editEmployee(id) {
    // -- Busca el empleado por id --
    const emp = employees.find(e => e.id == id);
    if (!emp) return;
    
    editingEmployeeId = id;
    // -- Carga sus datos en el formulario --
    employeeName.value = emp.name;
    employeeSurname.value = emp.surname;
    employeeEmail.value = emp.email;
    employeeDepartment.value = emp.department
    employeeActive.checked = emp.active; 
    // -- Activa el modo edición y muestra el botón de cancelar --
    employeeFormTitle.innerText = 'Editar empleado';
    cancelEmployeeBtn.style.display = 'inline';
}


// GUARDAR -- guarda un empleado y actualiza la interfaz según el resultado --
employeeForm.addEventListener('submit', async e => {
  e.preventDefault();
    // -- Evita el envío normal del formulario --
    const emp = {
        name: employeeName.value.trim(),
        surname: employeeSurname.value.trim(),
        email: employeeEmail.value.trim(),
        department: employeeDepartment.value.trim(),
        active: employeeActive.checked
    };
    // -- Valida los datos obligatorios --
    if (!emp.name || !emp.email) {
        showAlert('Nombre y email son obligatorios');
        return;
    }
    // -- Si está en edición, actualiza el empleado (PUT); si no, crea uno nuevo (POST) --
    try {
        if (editingEmployeeId !== null) {
        await apiPut(`/api/employees/${editingEmployeeId}`, emp);
        } else {
        await apiPost('/api/employees', emp);
        }
        // -- Recarga la lista, el dashboard y muestra un mensaje --
        resetEmployeeForm();
        await loadEmployees();
        loadDashboard();

        showAlert('Empleado guardado correctamente', 'success');

    } catch (error) {
        console.error(error);
        showAlert('Error guardando empleado');
    }
});

//ELIMINAR -- borra un empleado y actualiza la interfaz --
async function deleteEmployee(id) {          
// -- Pide confirmación al usuario --
if (!confirm('¿Eliminar empleado?')) return;
    try { // -- Elimina el empleado con DELETE --
        await apiDelete(`/api/employees/${id}`);
        // -- Recarga la lista y el dashboard --
        await loadEmployees();
        loadDashboard();
    } catch (error) {
       console.error(error);
       showAlert('Error eliminando empleado');
    }
}

//RESET -- deja el formulario listo para crear un nuevo empleado --
    async function resetEmployeeForm() {
    editingEmployeeId = null;
    employeeForm.reset();
    employeeFormTitle.innerText = 'Nuevo empleado';
    cancelEmployeeBtn.style.display = 'none';
}
cancelEmployeeBtn.addEventListener('click', resetEmployeeForm);

// ===========================================ACTIVOS (placeholder)================================
    //VARIABLES
    let assets = [];
    let editingAssetId = null;

    // Formulario y campos
    const assetsForm = document.getElementById('assetsForm');
    const assetsFormTitle = document.getElementById('assetsFormTitle');
    const cancelAssetBtn = document.getElementById('cancelAssetBtn');

    const assetType = document.getElementById('assetType');
    const assetBrand = document.getElementById('assetBrand');
    const assetModel = document.getElementById('assetModel');
    const assetSerial = document.getElementById('assetSerial');
    const assetState = document.getElementById('assetState');

    const assetsTableBody = document.querySelector('#assetsTable tbody');

// LOAD -- Carga y muestra la lista de activos --
async function loadAssets() {
  clearAlert();

  try {
    assets = await apiGet('/api/assets');
    renderAssets(assets);
    assetStateFilter.value = 'all';
  } catch (error) {
    console.error(error);
    showAlert('Error cargando activos');
  }
}

//RENDER -- pinta la tabla de activos a partir de una lista --
function renderAssets(list) {
    assetsTableBody.innerHTML = '';

    list.forEach(asset => {
        const tr = document.createElement('tr');
        tr.dataset.active = asset.state; // true / false

        tr.innerHTML = `
        <td>${asset.type}</td>
        <td>${asset.brand}</td>
        <td>${asset.model}</td>
        <td>${asset.serial}</td>
        <td>${asset.state}</td>
        <td>
            <button onclick="editAsset(${asset.id})">Editar</button>
            <button onclick="deleteAsset(${asset.id})">Eliminar</button>
            </td>
            `;

            assetsTableBody.appendChild(tr);
    });
}

//FILTRO -- filtra los activos visibles según el estado seleccionado --
assetStateFilter.addEventListener('change', () => {
    const filter = assetStateFilter.value;
    const rows = assetsTableBody.querySelectorAll('tr');

    rows.forEach(row => {
        row.style.display = 
        filter === 'all' || row.dataset.active === filter ? '' : 'none';
    });
});

//EDITAR -- prepara el formulario para editar un activo existente --

function editAsset(id) {
    const asset = assets.find(a => a.id === id);
    if (!asset) return;

    editingAssetId = id;

    assetType.value = asset.type;
    assetBrand.value = asset.brand;
    assetModel.value = asset.model;
    assetSerial.value = asset.serial;
    assetState.value = asset.state;

    assetsFormTitle.innerText = 'Editar activo';
    cancelAssetBtn.style.display = 'Inline';
}

// GUARDAR -- guarda un activo y actualiza la interfaz --
assetsForm.addEventListener('submit', async e => {
    e.preventDefault();
    // -- Evita el envío normal del formulario --
    const asset = {
        type: assetType.value.trim(),
        brand: assetBrand.value.trim(),
        model: assetModel.value.trim(),
        serial: assetSerial.value.trim(),
        state: assetState.value
    };
    // -- Evita el envío normal del formulario --
    if (!asset.type || !asset.serial) {
        showAlert('Tipo y serie obligatorios');
        return;
    }
    // -- Si está en edición, actualiza (PUT); si no, crea (POST) --
    try {
        if (editingAssetId !== null) {
        await apiPut(`/api/assets/${editingAssetId}`, asset);
        } else {
        await apiPost('/api/assets', asset);
        }
        // -- Recarga activos, dashboard y muestra mensaje --
        resetAssetForm();
        await loadAssets();
        loadDashboard();

        showAlert('Activo guardado correctamente', 'success');

    } catch (error) {
        console.error(error);
        showAlert('Error guardando activo');
    }
});

//ELIMINAR -- borra un activo y actualiza la interfaz --
async function deleteAsset(id) {
    if (!confirm('¿Eliminar activo?')) return;

    try {
        await apiDelete(`/api/assets/${id}`);
        await loadAssets();
        loadDashboard();
        showAlert('Activo eliminado', 'success');
    } catch (error) {
        console.error(error);
        showAlert('Error eliminando activo');
    }
}

//RESET -- deja el formulario listo para crear un nuevo activo --
function resetAssetForm() {
    editingAssetId = null;
    assetsForm.reset();
    assetsFormTitle.innerText = 'Nuevo activo';
    cancelAssetBtn.style.display = 'none';
}

cancelAssetBtn.addEventListener('click', resetAssetForm);


//================================= ASIGNACIÓN
// -- carga las asignaciones de un empleado desde la API --
async function fetchAssignments(empleadoId) {
    try { // -- Hace una petición GET para traer sus asignaciones --
        const res = await fetch(`/api/employees/${empleadoId}/assignments`);
        if(!res.ok)throw new Error(`Error ${res.status}: ${await res.text()}`);
        const assignments = await res.json();
        return assignments;
    } catch (err) {
        showAlert(err.message);
        return []; // -- Devuelve la lista o un array vacío --
    }
}

// RENDER -- muestra las asignaciones de un empleado en una tabla --
async function renderAssignments(empleadoId) {
    const tbody = document.querySelector('.Tabla2 tbody');
    tbody.innerHTML = '';

    if (!empleadoId) return;

    const assignments = await fetchAssignments(empleadoId);

    assignments.forEach(a => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${a.id}</td>
            <td>${a.assetType} ${a.assetBrand} ${a.assetModel} (${a.assetSerial})</td>
            <td>${a.startDate}</td>
            <td><button onclick="returnAsset(${a.id})">Devolver</button></td>
        `;
        tbody.appendChild(tr);
    });
}

//CAMBIO -- actualiza asignaciones y activos al cambiar de empleado --
// -- Escucha cuando se selecciona otro empleado --
document.getElementById('empleadoSelect').addEventListener('change', e => {
    const empleadoId = e.target.value;
    renderAssignments(empleadoId); //-- Carga sus asignaciones --
    renderActivosDispo(); // -- Muestra los activos disponibles --
});

//RETURN -- devuelve un activo asignado y actualiza la interfaz --
async function returnAsset(assignmentsId) { 
    // -- Pide confirmación al usuario --
    if (!confirm("¿Seguro que quieres devolver este activo?")) return;

    try { // -- Envía la devolución a la API (POST) --
        const res = await fetch(`/api/assignments/${assignmentsId}/return`, {
            method: 'POST'
        });
        if (!res.ok) { // -- Actualiza las asignaciones y los activos disponibles --
            const text = await res.text();
            throw new Error(`Error${res.status}:${text}`);
        } showAlert("Activo devuelto correctamente")
        
        const empleadoId = document.getElementById('empleadoSelect').value;
        renderAssignments(empleadoId);
        renderActivosDispo();
    } catch (err) {
        showAlert(err.message);
    }
}

//Obtener activos disponibles
async function fetchAvailableAssets() {
    try { // -- Hace una petición GET para traer los activos libres --
        const res = await fetch('/api/assets/available');
        if(!res.ok) throw new Error(`Error${res.status}:${await res.text()}`);
        return await res.json();
    } catch (err) {
        showAlert(err.message);
        return [];
    }
}

//RENDER -- muestra los activos disponibles para asignar --
async function renderActivosDispo() {
    const tbody = document.querySelector('.Tabla3 tbody');
    tbody.innerHTML ='';
    
    const activos = await fetchAvailableAssets();

    activos.forEach(a => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${a.type} ${a.brand} ${a.model} (${a.serial})</td>
            <td><button onclick="assignAsset(${a.id})">Asignar</button></td>
        `;

        tbody.appendChild(tr);
    });
}

//ASIGNAR -- asigna un activo a un empleado y actualiza la interfaz --
async function  assignAsset(activoId) {
    const empleadoId = document.getElementById('empleadoSelect').value;
    // -- Comprueba que haya un empleado seleccionado --
    if(!empleadoId) {
        showAlert("Seleccione un empleado");
        return;
    }

    try { // -- Envía la asignación a la API (POST) --
        const res = await fetch('/api/assignments', {
            method: 'POST',
            headers:{'Content-Type': 'application/json'},
            body: JSON.stringify({
                empleadoId: Number(empleadoId), 
                assetId: activoId
            })
        });

        if (res.status === 409) {
            showAlert("Error: el activo ya esta asignado");
            return;
        }
        if (!res.ok) throw new Error(`Error ${res.status}: ${await res.text()}`);
        showAlert("Activo asignado correctamente");

        renderAssignments(empleadoId);//-- Actualiza tabla asignaciones --
        renderActivosDispo();// -- Actualiza activos disponibles --
    } catch (err) {
        showAlert(err.message);
    }
}



// -- inicializa la vista al cargar la aplicación --
renderActivosDispo(); // -- Carga los activos disponibles --
renderDashboardChart() // -- Dibuja el gráfico del dashboard -- 


