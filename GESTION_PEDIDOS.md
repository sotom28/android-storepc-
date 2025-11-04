# Gestión de Pedidos - Documentación

## Descripción General

Se ha implementado un sistema completo de **CRUD** (Crear, Leer, Actualizar, Eliminar) para la gestión de pedidos en la aplicación StoreComponents. El administrador tiene control total sobre los pedidos con la capacidad de cambiar estados.

## Funcionalidades Implementadas

### 1. **CRUD Completo**

#### CREATE (Crear)
- ✅ Crear nuevos pedidos con:
  - Nombre del producto
  - Cantidad
  - Asignación automática al gestor de ventas
  - Estado inicial: **PENDIENTE**

#### READ (Leer)
- ✅ Visualizar lista completa de pedidos
- ✅ Ver detalles de cada pedido:
  - ID del pedido
  - Nombre del producto
  - Cantidad
  - Estado actual
  - Asignado a (nombre del gestor)
- ✅ Total de pedidos registrados

#### UPDATE (Actualizar)
- ✅ **Editar datos del pedido:**
  - Modificar nombre del producto
  - Cambiar cantidad
- ✅ **Cambiar estado del pedido:**
  - PENDIENTE → ENVIADO
  - PENDIENTE → ENTREGADO
  - PENDIENTE → CANCELADO
  - O cualquier transición entre estados

#### DELETE (Eliminar)
- ✅ Eliminar pedidos de la base de datos

### 2. **Estados de Pedidos**

Los cuatro estados disponibles son:

| Estado | Color | Significado |
|--------|-------|-------------|
| **PENDIENTE** | Secundario | Pedido registrado, en espera de procesamiento |
| **ENVIADO** | Terciario | Pedido despachado |
| **ENTREGADO** | Verde (primaryContainer) | Pedido entregado al cliente |
| **CANCELADO** | Rojo (errorContainer) | Pedido cancelado |

### 3. **Interfaz de Usuario**

#### Panel de Creación
- Campos de entrada validados
- Validación de cantidad > 0
- Validación de producto no vacío
- Botones "Crear Pedido" y "Volver"

#### Lista de Pedidos
- Visualización en cards con información completa
- Código de color para estados
- Botón "Cambiar estado" con dropdown menú
- Botones "Editar" y "Eliminar" para cada pedido
- Contador total de pedidos

#### Diálogo de Edición
- Modal para editar producto y cantidad
- Validación de datos
- Confirmación y cancelación

### 4. **Validaciones**

- ✅ El nombre del producto no puede estar vacío
- ✅ La cantidad debe ser mayor a 0
- ✅ Los datos editados deben ser válidos
- ✅ Confirmación visual con Toast después de acciones

## Archivos Modificados

### 1. **GestionPedidosScreen.kt**
- Actualización de la interfaz para CRUD completo
- Implementación de dropdown para cambio de estados
- Diálogo de edición de pedidos
- Mejor organización visual con Cards

### 2. **OrdersViewModel.kt**
- Nuevo método: `updateOrder(id, productName, quantity)`
- Método existente: `updateStatus(id, newStatus)`
- Método existente: `addOrder(productName, quantity, assignedToId)`
- Método existente: `removeOrder(id)`

## Flujo de Responsabilidades del Administrador

1. **Crear Pedidos**: El admin crea nuevos pedidos indicando producto y cantidad
2. **Asignar**: Los pedidos se asignan automáticamente al gestor de ventas
3. **Monitorear**: Visualiza todos los pedidos en tiempo real
4. **Editar**: Puede modificar producto o cantidad según necesidad
5. **Cambiar Estado**: 
   - PENDIENTE: Nuevo pedido
   - ENVIADO: Pedido en tránsito
   - ENTREGADO: Entregado al cliente
   - CANCELADO: Cancelado por cualquier razón
6. **Eliminar**: Puede eliminar pedidos si es necesario

## Cómo Usar

### Crear un Pedido
1. Ingresar en "Botón Pedidos" desde el menú Admin
2. Completar "Nombre del Producto"
3. Ingresar "Cantidad"
4. Hacer clic en "Crear Pedido"

### Cambiar Estado de un Pedido
1. Buscar el pedido en la lista
2. Hacer clic en "Cambiar estado: [ESTADO_ACTUAL]"
3. Seleccionar el nuevo estado del dropdown
4. El estado se actualiza inmediatamente

### Editar un Pedido
1. Hacer clic en botón "Editar" del pedido
2. Modificar producto y/o cantidad en el diálogo
3. Hacer clic en "Guardar"

### Eliminar un Pedido
1. Hacer clic en botón "Eliminar" (rojo) del pedido
2. El pedido se elimina inmediatamente

## Mensajes de Confirmación

- "Pedido creado exitosamente" - Cuando se crea un nuevo pedido
- "Estado actualizado a [ESTADO]" - Cuando cambia el estado
- "Pedido actualizado" - Cuando se editan datos
- "Pedido eliminado" - Cuando se elimina
- "Datos inválidos" - Cuando hay error en validación

## Próximas Mejoras (Opcionales)

- [ ] Persistencia en base de datos (SQLite/Room)
- [ ] Historial de cambios de estado
- [ ] Búsqueda y filtrado de pedidos
- [ ] Exportar pedidos a PDF/Excel
- [ ] Notificaciones automáticas
- [ ] Gráficos de estadísticas

