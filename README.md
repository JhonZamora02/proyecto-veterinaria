# VetSoft — Sistema de Gestión Veterinaria

Aplicación de escritorio desarrollada en **Java (Swing)** con base de datos **MySQL** para administrar la operación de una clínica veterinaria: mascotas, veterinarios, exámenes de laboratorio, órdenes de servicio y **facturación con generación de comprobantes en PDF**.

El sistema cubre el ciclo completo de atención: se registran la mascota y el veterinario, se crea una **orden de servicio** con los exámenes solicitados, se controlan abonos y saldos, y finalmente se **factura** la orden generando la factura con JasperReports.

---

## Tabla de contenido

- [Características](#características)
- [Flujo principal: Orden de Servicio](#flujo-principal-orden-de-servicio)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Requisitos](#requisitos)
- [Instalación y ejecución](#instalación-y-ejecución)
- [Base de datos](#base-de-datos)
- [Capturas de pantalla](#capturas-de-pantalla)
- [Limitaciones conocidas y mejoras sugeridas](#limitaciones-conocidas-y-mejoras-sugeridas)

---

## Características

- **Inicio de sesión** con validación de credenciales contra la base de datos (solo usuarios en estado *Activo*).
- **Gestión de usuarios y perfiles** para controlar quién accede al sistema.
- **Catálogos maestros (CRUD):** especies, razas, mascotas, veterinarios, formas de pago y conceptos.
- **Exámenes de laboratorio:** registro de exámenes con su tipo y valor, y definición de sus **parámetros** (unidad, fórmula y valores de referencia).
- **Órdenes de servicio:** creación de órdenes asociadas a un veterinario y una mascota, con detalle de exámenes, cantidades, subtotales, abono y saldo.
- **Listado y consulta de órdenes** con filtros por rango de fechas, número de orden, estado, veterinario, mascota y examen.
- **Facturación** de órdenes con factura generada en **JasperReports**.
- **Exportación a PDF** del listado de órdenes de servicio.
- Menú principal con bienvenida al usuario y cierre de sesión.

## Flujo principal: Orden de Servicio

```
1. Crear orden      Se selecciona veterinario y mascota, se agregan los exámenes
                    y se registra el abono la orden queda "En proceso".

2. Consultar        Desde "Listar órdenes" se filtra y revisa el detalle de cada orden.
                    Las órdenes facturadas se resaltan en verde.

3. Facturar         Se genera la factura (JasperReports) y, tras confirmar,
                    la orden pasa a estado "Facturado".
                    Una orden facturada no puede volver a facturarse.

4. Exportar         El listado filtrado puede exportarse a PDF.
```

## Arquitectura

El proyecto sigue el patrón **MVC con capa DAO**, organizado por paquetes:

| Paquete | Responsabilidad |
|---------|-----------------|
| `View` | Formularios Swing (`frm*`), diseñados con el editor de NetBeans |
| `Controller` | Controladores (`Ctrl*`) que enlazan las vistas con los DAO y manejan los eventos |
| `DAO` | Acceso a datos con JDBC y consultas parametrizadas (`PreparedStatement`) |
| `Model` | Clases de entidad (POJOs): `Mascota`, `Veterinario`, `OrdenServicio`, `Examen`, etc. |
| `Report` | Plantillas JasperReports (`.jrxml` / `.jasper`) |
| `Compilador` | Clase principal (`Compilador.java`) que lanza el login |
| `Imagenes` | Íconos y logos de la interfaz |

## Tecnologías

| Área | Tecnología |
|------|-----------|
| Lenguaje | Java (compatibilidad 1.8) |
| Interfaz | Java Swing |
| Base de datos | MySQL + JDBC (MySQL Connector/J) |
| Reportes | JasperReports 6.20 |
| PDF | OpenPDF / iText |
| Utilidades | Apache Commons (beanutils, collections4, digester, io, logging), Groovy |
| IDE / build | NetBeans + Apache Ant |

## Estructura del proyecto

```
proyecto-veterinaria/
├── src/
│   ├── Compilador/       # Punto de entrada (main)
│   ├── Controller/       # CtrlLogin, CtrlMenu, CtrlOrdenServicio, CtrlMascota, ...
│   ├── DAO/              # MySQLConnection, OrdenServicioDAO, MascotaDAO, ...
│   ├── Model/            # Entidades del dominio
│   ├── View/             # Formularios Swing (.java y .form)
│   ├── Report/           # Factura_OrdenServicio.jrxml / .jasper
│   └── Imagenes/         # Recursos gráficos
├── docs/images_tests/    # Capturas de pantalla de las pruebas
├── nbproject/            # Configuración del proyecto NetBeans
├── build.xml             # Script de compilación Ant
└── manifest.mf
```

## Requisitos

- **JDK 8** o superior
- **MySQL 5.7+ / MariaDB** en ejecución (puerto `3306`)
- **NetBeans** (recomendado) para abrir y ejecutar el proyecto
- Librerías: JasperReports 6.20, OpenPDF/iText, MySQL Connector/J, Apache Commons y Groovy (se copian a `dist/lib` al compilar)

## Instalación y ejecución

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/JhonZamora02/proyecto-veterinaria.git
   ```

2. **Crear la base de datos** `labpets` en MySQL con las tablas descritas en [Base de datos](#base-de-datos).

3. **Configurar la conexión** en `src/DAO/MySQLConnection.java`:
   ```java
   public static final String puerto      = "3306";
   public static final String nomservidor = "localhost";
   public static final String db          = "labpets";
   private static final String user       = "root";
   private static final String pass       = "";
   ```

4. **Abrir el proyecto en NetBeans** (`File → Open Project`) y agregar las librerías `.jar` a las dependencias del proyecto si NetBeans no las encuentra.

5. **Ejecutar** el proyecto (clase principal `Compilador.Compilador`). Se abrirá la ventana de inicio de sesión.

   > La factura se carga desde `src/Report/Factura_OrdenServicio.jasper` usando la carpeta de trabajo (`user.dir`). Por eso, **ejecuta el programa desde la raíz del proyecto** (como lo hace NetBeans) para que el reporte se encuentre.

## Base de datos

El sistema usa la base de datos **`labpets`**. Las tablas que utiliza el código son:

| Tabla | Descripción |
|-------|-------------|
| `usuario` | Usuarios del sistema (login, contraseña, datos personales, estado, perfil) |
| `perfil` | Perfiles de acceso |
| `especie` / `raza` | Catálogo de especies y razas (cada raza pertenece a una especie) |
| `mascota` | Mascotas con nombre, edad, sexo, propietario y raza |
| `veterinario` | Veterinarios y la veterinaria a la que pertenecen |
| `terceros` | Terceros / proveedores |
| `examen` | Exámenes con tipo, valor y estado |
| `parametros` / `examen_parametros` | Parámetros de cada examen (unidad, fórmula, referencias) |
| `orden_servicio` | Encabezado de la orden: fecha, veterinario, mascota, total, abono, saldo y estado |
| `orden_servicio_detalle` | Exámenes de cada orden con valor, cantidad y subtotal |
| `forma_pago` / `concepto` | Catálogos de pago y conceptos |

## Capturas de pantalla

Las capturas de las pruebas se encuentran en [`docs/images_tests/`](docs/images_tests/):

| Módulo | Archivos |
|--------|----------|
| Acceso | `inicio_sesion.png`, `inicio_sesion_exitoso.png`, `menu_principal.png` |
| Usuarios y perfiles | `crud_usuario.png`, `crud_perfil.png`, `crud_usuario_perfil.png` |
| Catálogos | `crud_especie.png`, `crud_raza.png`, `crud_mascota.png`, `crud_veterinaria.png`, `crud_proveedor.png`, `crud_formapago.png`, `crud_concepto.png` |
| Exámenes | `crud_examen.png`, `crud_examen_tipoexamen.png`, `crud_parametro_examen.png` |
| Órdenes de servicio | `crud_ordenservicio_mascota.png`, `crud_ordenservicio_veterinaria.png`, `crud_ordenservicio_examen.png`, `crud_ordenservicio_prueba.png`, `crud_ordenservicio_pruebaexitosa.png` |
| Listado y facturación | `crud_listar_ordenes_servicio.png`, `crud_listar_ordenes_servicio_exportarPDF.png`, `facturar_ordenservicio.png`, `facturar_ordenservicio_exitoso.png`, `factura_generada_jasperreport.png` |

## Limitaciones conocidas y mejoras sugeridas

- **Contraseñas:** se guardan y comparan en texto plano. Se recomienda almacenarlas con hash (por ejemplo, BCrypt).
- **Credenciales de BD:** están escritas directamente en `MySQLConnection.java`; conviene moverlas a un archivo de configuración.
- **Comprobante de egreso:** la opción existe en el menú (*Documento → Comprobante de Egreso*), pero aún no tiene funcionalidad.
- **Script SQL:** el repositorio no incluye el script de creación de la base de datos; agregarlo facilitaría la instalación.
- **Ruta del reporte:** la factura depende de la carpeta de trabajo; se puede mejorar cargándola como recurso del `.jar`.
- **Configuración del IDE:** `nbproject/private` y `project.properties` contienen rutas locales del equipo; se recomienda excluirlas del control de versiones.

## Autor

Desarrollado por **JhonZamora02**.
