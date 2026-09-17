package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ADAPTER.ProductoSeleccionadoAdapter;
import com.example.myapplication.ADAPTER.buscarClienteAdapter;
import com.example.myapplication.ADAPTER.buscarProductoAdapter;
import com.example.myapplication.DAO.FacturaDao;
import com.example.myapplication.Entity.ClienteEntity;
import com.example.myapplication.Entity.DetalleFacturaEntity;
import com.example.myapplication.Entity.FacturaEntity;
import com.example.myapplication.Entity.ProductoEntity;
import com.example.myapplication.ROOM.productosdb;
import com.example.myapplication.TICKET.PdfUtil;
import com.example.myapplication.TICKET.UrovoPrintHelper;
import com.example.myapplication.modelos.Cliente;
import com.example.myapplication.modelos.Producto;
import com.example.myapplication.notificaciones.NotificationHelper;
import com.example.myapplication.sharedPreferences.SessionManager;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class crearfactura extends AppCompatActivity {

    EditText etBuscarCliente;
    RecyclerView rvClientes;
    TextView tvClienteInfo, tvSinResultados;
    buscarClienteAdapter adapter;
    RecyclerView rvProductos;
    TextView tvTotal,tvPropina,tvsubtotal;
    buscarProductoAdapter productoAdapter;
    List<Producto> listaProductos = new ArrayList<>();
    EditText etBuscarProducto;
    TextView tvSeleccionados;
    Button btnConfirmar,btnCancelar;
    LinearLayout Lytotales;
    RadioGroup radioGroup;

    private Cliente clienteSeleccionado;
    private RecyclerView recyclerSeleccionados;
    private ProductoSeleccionadoAdapter adapterSeleccionados;
    private List<Producto> productosSeleccionados = new ArrayList<>();

    private FacturaEntity factura;
    SessionManager sessionManager;
    String codigoG;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crearfactura);

        radioGroup=findViewById(R.id.radioGroup);

        productosdb dbfactura = productosdb.getInstancia(this);
        factura = new FacturaEntity();

        //obtener fecha actual
        LocalDate fechaActual = LocalDate.now();
        String fechaFormateada = fechaActual.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        //obtener hora actual
        LocalTime horaActual = LocalTime.now();
        String horaFormateada = horaActual.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        // genera un GUID para la factura
        sessionManager = new SessionManager(this);
         codigoG=UUID.randomUUID().toString().toUpperCase();
        factura.codigoGeneracion = codigoG;
        factura.fecEmi=fechaFormateada;
        factura.horEmi=horaFormateada;
        factura.estado="NO ENVIADO";
        factura.sucursal= sessionManager.getSucursal();
        factura.Name=sessionManager.getName();
        factura.condicionOperacion= String.valueOf(1);
        // Obtener el último ID actual
        asignarNuevoIdFactura(() -> {


        });



        NotificationHelper.crearCanalNotificacion(this);




        //buscar cliente y producto
        etBuscarCliente = findViewById(R.id.etBuscarCliente);
        etBuscarProducto = findViewById(R.id.etBuscarProducto);

        //recyclerview donde se muestran los productos a buscar
        rvClientes = findViewById(R.id.rvClientes);
        rvProductos = findViewById(R.id.rvProductos);
        //aqui se guardan la informacion del cliente
        tvClienteInfo = findViewById(R.id.tvClienteInfo);


        tvSinResultados = findViewById(R.id.tvSinResultados);
        tvSeleccionados=findViewById(R.id.tvSeleccionados);
        btnConfirmar = findViewById(R.id.btnConfirmarFactura);
        btnCancelar=findViewById(R.id.btnCancelar);
        //ocultar informacion del cliente mientras no se hace la busqueda
        tvClienteInfo.setVisibility(View.GONE);
        tvSinResultados.setVisibility(View.GONE);

        //el total de la factura
        tvTotal = findViewById(R.id.tvTotal);
        tvPropina=findViewById(R.id.tvpropina);
        tvsubtotal=findViewById(R.id.tvsubtotal);

        Lytotales=findViewById(R.id.Lytotales);

        //metodos para descargar automaticamente clientes y productos
        obtenerClientesDesdeRoomAsync();
        obtenerProductosDesdeRoomAsync();

        //inicializa el recycler view que se ocupa para mostrar los items seleccionados del recyclerview rvproductos
        recyclerSeleccionados = findViewById(R.id.rvProductosSeleccionados);
        recyclerSeleccionados.setLayoutManager(new LinearLayoutManager(this));
        adapterSeleccionados = new ProductoSeleccionadoAdapter(productosSeleccionados, new ProductoSeleccionadoAdapter.OnProductoModificadoListener() {
            @Override
            public void onProductoActualizado() {
                actualizarTotal();
            }

            @Override
            public void onProductoEliminado(Producto producto) {
                // NUEVO: poner cantidad en 0 en la lista original
                for (Producto p : listaProductos) {
                    if (p.getIdProducto() == producto.getIdProducto()) {
                        p.setCantidad(0);
                        break;
                    }
                }
                // Actualiza la lista visual y total
                actualizarTotal();
                productoAdapter.notifyDataSetChanged();
            }
        });
        recyclerSeleccionados.setAdapter(adapterSeleccionados);




        etBuscarProducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (productoAdapter != null) {
                    productoAdapter.getFilter().filter(s.toString());

                    // Mostrar productos solo si hay texto ingresado
                    if (s.toString().trim().isEmpty() || productoAdapter.getItemCount()==0) {
                        rvProductos.setVisibility(View.GONE);
                        etBuscarCliente.setVisibility(View.VISIBLE);
                        tvSeleccionados.setVisibility(View.VISIBLE);
                        tvClienteInfo.setVisibility(View.VISIBLE);
                        Lytotales.setVisibility(View.VISIBLE);
                        recyclerSeleccionados.setVisibility(View.VISIBLE);




                    } else {
                        rvProductos.setVisibility(View.VISIBLE);
                        etBuscarCliente.setVisibility(View.GONE);
                        tvSeleccionados.setVisibility(View.GONE);
                        tvClienteInfo.setVisibility(View.GONE);
                        Lytotales.setVisibility(View.GONE);
                        recyclerSeleccionados.setVisibility(View.GONE);

                    }
                }
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        //salir
        btnCancelar.setOnClickListener(v ->{
            new AlertDialog.Builder(this)
                    .setTitle("Confirmación")
                    .setMessage("¿Deseas salir? Si lo haces, perderás los datos de esta factura.")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Sí", (dialog, which) -> {
                        Intent intent = new Intent(this, menu.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish(); 
                    })

                    .show();
        });



        btnConfirmar.setOnClickListener(v -> {

            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(crearfactura.this, "Debes seleccionar un metodo de pago", Toast.LENGTH_SHORT).show();
                return;
            } else {
                RadioButton selectedRadio = findViewById(selectedId);
                factura.metodo_pagos = selectedRadio.getText().toString();
            }

            if (clienteSeleccionado == null) {
                Toast.makeText(this, "Seleccione un cliente", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Producto> seleccionados = new ArrayList<>();
            for (Producto p : productosSeleccionados) {
                if (p.getCantidad() > 0) {
                    seleccionados.add(p);
                }
            }

            if (seleccionados.isEmpty()) {
                Toast.makeText(this, "No hay productos seleccionados", Toast.LENGTH_SHORT).show();
                return;
            }

            actualizarTotal();

            if (factura.totalPagar <= 0) {
                Toast.makeText(this, "Error: Total a pagar es 0. Verifique los productos.", Toast.LENGTH_LONG).show();
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Confirmar factura")
                    .setMessage("¿Está seguro que desea guardar esta factura?")
                    .setPositiveButton("Sí", (dialog, which) -> {

                        // Hacer copia de productos seleccionados
                        List<Producto> copiaSeleccionados = new ArrayList<>();
                        for (Producto p : seleccionados) {
                            Producto copia = new Producto();
                            copia.setIdProducto(p.getIdProducto());
                            copia.setDescripcion(p.getDescripcion());
                            copia.setPrecio(p.getPrecio());
                            copia.setPropina(p.getPropina());
                            copia.setCantidad(p.getCantidad());
                            copia.setCodigoProducto(p.getCodigoProducto());
                            copia.setUnidadMedida(p.getUnidadMedida());
                            copiaSeleccionados.add(copia);
                        }

                        // CAPTURAR EL CODIGO DE GENERACION ANTES DE LIMPIAR
                        String codigoGeneracionActual = factura.codigoGeneracion;

                        // Guardar la factura y detalles
                        guardarFacturaYDetalles(factura, copiaSeleccionados);

                        // Consultar detalles y generar PDF después de guardar
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            productosdb db = productosdb.getInstancia(getApplicationContext());
                            List<DetalleFacturaEntity> detalleFacturaList = db.detalleFacturaDao().getDetallesPorFactura(codigoGeneracionActual);

                      /*      File pdfFile = PdfUtil.generarTicketCompletoPDF(crearfactura.this, factura, detalleFacturaList);

                            if (pdfFile != null && pdfFile.exists()) {
                                PdfUtil.verPdf(crearfactura.this, pdfFile);
                            }

                      */
                            UrovoPrintHelper urovoPrintHelper = new UrovoPrintHelper(v.getContext());
                            urovoPrintHelper.printFacturaRoom(factura,detalleFacturaList);

                            // Ahora sí, limpiar el formulario
                            limpiarFormularioFactura();

                        }, 100);  // Tiempo de espera para asegurarse que se haya guardado (ajusta si es necesario)
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });





    }

    private void limpiarFormularioFactura() {
        Cliente clientePrevio = clienteSeleccionado;

        // Limpiar productos
        for (Producto p : listaProductos) {
            p.setCantidad(0);
        }

        productosSeleccionados.clear();
        adapterSeleccionados.notifyDataSetChanged();
        productoAdapter.notifyDataSetChanged();

        // Limpiar cliente UI
        etBuscarCliente.setText("");
        tvClienteInfo.setVisibility(View.GONE);
        clienteSeleccionado = null;

        // Crear nueva Factura
        factura = new FacturaEntity();
        factura.codigoGeneracion = UUID.randomUUID().toString().toUpperCase();
        factura.fecEmi = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        factura.horEmi = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        factura.estado = "NO ENVIADO";
        factura.sucursal = sessionManager.getSucursal();
        factura.Name = sessionManager.getName();
        factura.condicionOperacion = "1";

        asignarNuevoIdFactura(() -> {
            productosdb db = productosdb.getInstancia(getApplicationContext());
            int ultimoId = db.facturaDao().getUltimoIdFactura();
            factura.setIdFactura(ultimoId + 1);
        });

        actualizarTotal();

        // Restaurar cliente (opcional)
        if (clientePrevio != null) {
            clienteSeleccionado = clientePrevio;
            factura.tipoDocumento = clientePrevio.getTipoDocumento();
            factura.numDocumento = clientePrevio.getNumDocumento();
            factura.nrc = clientePrevio.getNrc();
            factura.nombre = clientePrevio.getNombre();
            factura.nombreComercial = clientePrevio.getNombreComercial();
            factura.idDepartamento = clientePrevio.getIdDepartamento();
            factura.idMunicipio = clientePrevio.getIdMunicipio();
            factura.direccionComplemento = clientePrevio.getDireccionComplemento();
            factura.telefono = clientePrevio.getTelefono();
            factura.correo = clientePrevio.getCorreo();
            factura.idActividadEconomica = clientePrevio.getIdActividadEconomica();
            factura.tipoEstablecimiento_idTipoEstablecimiento = clientePrevio.getTipoEstablecimiento_idTipoEstablecimiento();
            factura.departamento = clientePrevio.getDepartamento();
            factura.municipio = clientePrevio.getMunicipio();
            factura.codigo_departamento = clientePrevio.getCodigo_departamento();
            factura.codigo_municipio = clientePrevio.getCodigo_municipio();
            factura.codActividad = clientePrevio.getCodActividad();
            factura.descActividad = clientePrevio.getDescActividad();

            tvClienteInfo.setText("Cliente seleccionado: " + clientePrevio.getNombre());
            tvClienteInfo.setVisibility(View.VISIBLE);
        }
    }


    public void agregarProductoSeleccionado(Producto producto) {
        Producto productoOriginal = null;
        for (Producto p : listaProductos) {
            if (p.getIdProducto() == producto.getIdProducto()) {
                productoOriginal = p;

                break;
            }
        }

        if (productoOriginal == null) return;

        boolean encontrado = false;
        for (Producto p : productosSeleccionados) {
            if (p.getIdProducto() == productoOriginal.getIdProducto()) {
                p.setCantidad(p.getCantidad() + 1);
                encontrado = true;
                //ocultar el teclado
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                View view = this.getCurrentFocus(); // obtiene la vista con foco actual
                if (imm != null && view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                break;
            }
        }

        if (!encontrado) {
            productoOriginal.setCantidad(1);
            productosSeleccionados.add(productoOriginal);

            //ocultar el teclado
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = this.getCurrentFocus(); // obtiene la vista con foco actual
            if (imm != null && view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        }

        adapterSeleccionados.notifyDataSetChanged();
        productoAdapter.notifyDataSetChanged();
        actualizarTotal();

        rvProductos.setVisibility(View.GONE);
        etBuscarProducto.setText("");
    }







    private void obtenerClientesDesdeRoomAsync() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Log.d("DEBUG_DB", "Iniciando consulta a Room");
                productosdb db = productosdb.getInstancia(getApplicationContext());
                List<ClienteEntity> entidades = db.clienteDao().getTodosLosClientes();

                List<Cliente> clientes = new ArrayList<>();

                for (ClienteEntity entity : entidades) {
                    Log.d("DEBUG_DB", "ClienteEntity: " + entity.getNombre() + ", " + entity.getCorreo());
                    Cliente cliente = new Cliente();

                    cliente.setIdCliente(entity.getIdCliente());
                    cliente.setTipoDocumento(entity.getTipoDocumento());
                    cliente.setNumDocumento(entity.getNumDocumento());
                    cliente.setNrc(entity.getNrc());
                    cliente.setNombre(entity.getNombre());
                    cliente.setNombreComercial(entity.getNombreComercial());
                    cliente.setIdDepartamento(entity.getIdDepartamento());
                    cliente.setIdMunicipio(entity.getIdMunicipio());
                    cliente.setDireccionComplemento(entity.getDireccionComplemento());
                    cliente.setTelefono(entity.getTelefono());
                    cliente.setCorreo(entity.getCorreo());
                    cliente.setIdActividadEconomica(entity.getIdActividadEconomica());
                    cliente.setCodigoCliente(entity.getCodigoCliente());
                    cliente.setTipoEstablecimiento_idTipoEstablecimiento(entity.getTipoEstablecimiento_idTipoEstablecimiento());
                    cliente.setDepartamento(entity.getDepartamento());
                    cliente.setMunicipio(entity.getMunicipio());
                    cliente.setCodigo_departamento(entity.getCodigo_departamento());
                    cliente.setCodigo_municipio(entity.getCodigo_municipio());
                    cliente.setCodActividad(entity.getCodActividad());
                    cliente.setDescActividad(entity.getDescActividad());
                    clientes.add(cliente);
                }

                runOnUiThread(() -> {
                    Log.d("DEBUG_UI", "Mostrando lista de clientes en UI");
                    if (clientes.isEmpty()) {
                        tvSinResultados.setVisibility(View.VISIBLE);
                    } else {
                        tvSinResultados.setVisibility(View.GONE);
                    }

                    adapter = new buscarClienteAdapter(clientes, cliente -> {
                        clienteSeleccionado = cliente; // GUARDAR
                        // Asignar datos del cliente a la factura
                       factura.tipoDocumento = cliente.getTipoDocumento();
                        factura.numDocumento = cliente.getNumDocumento();
                        factura.nrc = cliente.getNrc();
                        factura.nombre = cliente.getNombre();
                        factura.nombreComercial = cliente.getNombreComercial();
                        factura.idDepartamento = cliente.getIdDepartamento();
                        factura.idMunicipio = cliente.getIdMunicipio();
                        factura.direccionComplemento = cliente.getDireccionComplemento();
                        factura.telefono = cliente.getTelefono();
                        factura.correo = cliente.getCorreo();
                        factura.idActividadEconomica = cliente.getIdActividadEconomica();
                        factura.tipoEstablecimiento_idTipoEstablecimiento = cliente.getTipoEstablecimiento_idTipoEstablecimiento();
                        factura.departamento = cliente.getDepartamento();
                        factura.municipio=cliente.getMunicipio();
                        factura.codigo_departamento=cliente.getCodigo_departamento();
                        factura.codigo_municipio=cliente.getCodigo_municipio();
                        factura.codActividad=cliente.getCodActividad();
                        factura.descActividad=cliente.getDescActividad();

                        //fin de obtener datos del cliente

                        tvClienteInfo.setText("Cliente seleccionado: " + cliente.getNombre()+" - "+cliente.getNumDocumento());
                        etBuscarCliente.setText("");
                        tvClienteInfo.setVisibility(View.VISIBLE);
                        rvClientes.setVisibility(View.GONE);
                    });

                    rvClientes.setAdapter(adapter);
                    rvClientes.setLayoutManager(new LinearLayoutManager(this));

                    // Buscar "Clientes varios"
                    Cliente clienteVarios = null;
                    for (Cliente c : clientes) {
                        if ("Clientes varios".equalsIgnoreCase(c.getNombre())) {
                            clienteVarios = c;
                            break;
                        }
                    }

                    if (clienteVarios != null) {
                        clienteSeleccionado = clienteVarios;
                        // Asignar datos del cliente a la factura
                       factura.tipoDocumento = clienteVarios.getTipoDocumento();
                        factura.numDocumento = clienteVarios.getNumDocumento();
                        factura.nrc = clienteVarios.getNrc();
                        factura.nombre = clienteVarios.getNombre();
                        factura.nombreComercial = clienteVarios.getNombreComercial();
                        factura.idDepartamento = clienteVarios.getIdDepartamento();
                        factura.idMunicipio = clienteVarios.getIdMunicipio();
                        factura.direccionComplemento = clienteVarios.getDireccionComplemento();
                        factura.telefono = clienteVarios.getTelefono();
                        factura.correo = clienteVarios.getCorreo();
                        factura.idActividadEconomica = clienteVarios.getIdActividadEconomica();
                        factura.tipoEstablecimiento_idTipoEstablecimiento = clienteVarios.getTipoEstablecimiento_idTipoEstablecimiento();
                        factura.departamento = clienteVarios.getDepartamento();
                        factura.municipio=clienteVarios.getMunicipio();
                        factura.codigo_departamento=clienteVarios.getCodigo_departamento();
                        factura.codigo_municipio=clienteVarios.getCodigo_municipio();
                        factura.codActividad=clienteVarios.getCodActividad();
                        factura.descActividad=clienteVarios.getDescActividad();
                        //fin de obtener datos del cliente
                        tvClienteInfo.setText("Cliente seleccionado: " + clienteVarios.getNombre());
                        tvClienteInfo.setVisibility(View.VISIBLE);
                        
                    }

                    etBuscarCliente.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.toString().trim().isEmpty()) {
                                rvClientes.setVisibility(View.GONE);
                                tvSinResultados.setVisibility(View.GONE);
                            } else {
                                rvClientes.setVisibility(View.VISIBLE);
                                if (adapter != null) {
                                    adapter.getFilter().filter(s);
                                    new Handler().postDelayed(() -> {
                                        if (adapter.getFilteredItemCount() == 0) {
                                            tvSinResultados.setVisibility(View.VISIBLE);
                                        } else {
                                            tvSinResultados.setVisibility(View.GONE);
                                        }
                                    }, 100);
                                }
                            }
                        }

                        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                        @Override public void afterTextChanged(Editable s) {}
                    });
                });
            } catch (Exception e) {
                Log.e("ERROR_DB", "Error al obtener clientes: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(crearfactura.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void obtenerProductosDesdeRoomAsync() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                productosdb db = productosdb.getInstancia(getApplicationContext());
                List<ProductoEntity> entidades = db.productoDao().getTodosLosProductos();

                listaProductos.clear();
                for (ProductoEntity entity : entidades) {
                    Producto producto = new Producto();
                    producto.setIdProducto(entity.getIdProducto());
                    producto.setDescripcion(entity.getDescripcion());
                    producto.setPrecio(entity.getPrecio());
                    producto.setPropina(entity.getPropina()); // propina
                    producto.setUnidadMedida(entity.getUnidadMedida());


                    producto.setCodigoProducto(entity.getCodigoProducto());

                    // Si necesitas más campos, agrégalos aquí también
                    listaProductos.add(producto);
                }

                runOnUiThread(() -> {
                    productoAdapter = new buscarProductoAdapter(listaProductos);


                    productoAdapter.setOnProductoClickListener(producto -> {
                        agregarProductoSeleccionado(producto);
                    });





                    rvProductos.setAdapter(productoAdapter);
                    rvProductos.setLayoutManager(new LinearLayoutManager(this));
                });


            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(crearfactura.this, "Error al obtener productos", Toast.LENGTH_SHORT).show());
            }
        });
    }


    private void asignarNuevoIdFactura(Runnable onComplete) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            productosdb db = productosdb.getInstancia(getApplicationContext());
            int ultimoId = db.facturaDao().getUltimoIdFactura();
            factura.setIdFactura(ultimoId + 1);
            Log.d("FACTURA_ID", "Nuevo ID asignado: " + factura.getIdFactura());

            // Ejecutar continuación en UI
            new Handler(Looper.getMainLooper()).post(onComplete);
        });
    }


    private void actualizarTotal() {
        double subtotal = 0.0;
        double propina = 0.0;
        double total = 0.0;
        double iva = 0.0;
        double tasaIva = 0.13;

        for (Producto p : productosSeleccionados) {
            int cantidad = p.getCantidad();
            double precioConIva = p.getPrecio();

            // Extraer IVA incluido por unidad
            double ivaUnidad = precioConIva - (precioConIva / (1 + tasaIva));
            iva += ivaUnidad * cantidad; // sin redondear aquí

            subtotal += precioConIva * cantidad;
            propina += p.getPropina() * cantidad;
        }

        // Redondeo del IVA total al final
        iva = Math.round(iva * 100.0) / 100.0;

        total = subtotal + propina;
        total = Math.round(total * 100.0) / 100.0;

        // Mostrar en pantalla
        tvsubtotal.setText("SubTotal: $" + String.format("%.2f", subtotal));
        tvPropina.setText("Propina: $" + String.format("%.2f", propina));
        tvTotal.setText("Total: $" + String.format("%.2f", total));

        // Asignar valores a la factura
        factura.totalGravado = subtotal;
        factura.subTotalVentas = subtotal;
        factura.subtotal = subtotal;
        factura.totalNoGravado = propina;
        factura.totalPagar = total;
        factura.totalIva = iva;
        factura.totalLetras = convertirNumeroALetras(total);





    }




    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Confirmación")
                .setMessage("¿Deseas salir? Si lo haces, perderás los datos de esta factura.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Sí", (dialog, which) -> {
                    Intent intent = new Intent(this, menu.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // <-- Cierra esta actividad
                    super.onBackPressed();
                })
                .show();
    }



    public static String convertirNumeroALetras(double numero) {
        long parteEntera = (long) numero;
        int centavos = (int) Math.round((numero - parteEntera) * 100);

        return numeroALetras(parteEntera) + " dólares con " + String.format("%02d", centavos) + "/100";
    }
    public static String numeroALetras(long numero) {
        String[] unidades = {
                "", "uno", "dos", "tres", "cuatro", "cinco",
                "seis", "siete", "ocho", "nueve", "diez",
                "once", "doce", "trece", "catorce", "quince",
                "dieciséis", "diecisiete", "dieciocho", "diecinueve", "veinte"
        };

        String[] decenas = {
                "", "", "veinte", "treinta", "cuarenta", "cincuenta",
                "sesenta", "setenta", "ochenta", "noventa"
        };

        String[] centenas = {
                "", "ciento", "doscientos", "trescientos", "cuatrocientos",
                "quinientos", "seiscientos", "setecientos", "ochocientos", "novecientos"
        };

        if (numero == 0) return "cero";
        if (numero == 100) return "cien";

        StringBuilder resultado = new StringBuilder();

        if (numero >= 1_000_000) {
            resultado.append(numeroALetras(numero / 1_000_000))
                    .append(numero / 1_000_000 == 1 ? " millón " : " millones ");
            numero %= 1_000_000;
        }

        if (numero >= 1000) {
            if (numero / 1000 == 1) {
                resultado.append("mil ");
            } else {
                resultado.append(numeroALetras(numero / 1000)).append(" mil ");
            }
            numero %= 1000;
        }

        if (numero >= 100) {
            resultado.append(centenas[(int) (numero / 100)]).append(" ");
            numero %= 100;
        }

        if (numero > 20) {
            resultado.append(decenas[(int) (numero / 10)]);
            if (numero % 10 != 0) {
                resultado.append(" y ").append(unidades[(int) (numero % 10)]);
            }
        } else {
            resultado.append(unidades[(int) numero]);
        }

        return resultado.toString().trim();
    }

    private void guardarFacturaYDetalles(FacturaEntity factura, List<Producto> productosSeleccionados) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                if (productosSeleccionados == null || productosSeleccionados.isEmpty()) {
                    Log.e("GUARDAR_FACTURA", "⚠️ No hay productos seleccionados, no se guardará la factura");
                    runOnUiThread(() ->
                            Toast.makeText(crearfactura.this, "No se puede guardar una factura sin productos", Toast.LENGTH_SHORT).show()
                    );
                    return; // 🚫 Sale y no guarda nada
                }

                productosdb db = productosdb.getInstancia(getApplicationContext());

                // Filtrar productos con cantidad > 0
                List<DetalleFacturaEntity> detalles = new ArrayList<>();
                int numItem = 1;

                for (Producto p : productosSeleccionados) {
                    if (p.getCantidad() <= 0) continue;

                    DetalleFacturaEntity detalle = new DetalleFacturaEntity();
                    detalle.codigoGeneracion = factura.codigoGeneracion;
                    detalle.numItem = numItem++;
                    detalle.tipoItem = 1;
                    detalle.cantidad = p.getCantidad();
                    detalle.codigo = p.getCodigoProducto();
                    detalle.descripcion = p.getDescripcion();
                    detalle.precioUni = p.getPrecio();
                    detalle.uniMedida = p.getUnidadMedida();

                    // Calcular montos
                    detalle.ventaGravada = p.getPrecio() * p.getCantidad();
                    detalle.noGravado = p.getPropina() * p.getCantidad();
                    detalle.ivaItem = Math.round((detalle.ventaGravada * 0.13) / (1 + 0.13) * 100.0) / 100.0;

                    Log.d("GUARDAR_FACTURA", "Detalle: " + detalle.descripcion + " Cantidad: " + detalle.cantidad);
                    detalles.add(detalle);
                }

                if (detalles.isEmpty()) {
                    Log.e("GUARDAR_FACTURA", "⚠️ Ningún producto tiene cantidad válida, no se guardará la factura");
                    runOnUiThread(() ->
                            Toast.makeText(crearfactura.this, "Todos los productos tienen cantidad 0, no se guardó la factura", Toast.LENGTH_SHORT).show()
                    );
                    return; // 🚫 Sale y no guarda nada
                }

                // ✅ Ahora sí: primero guarda la factura
                db.facturaDao().insertarFactura(factura);

                // ✅ Luego guarda los detalles
                db.detalleFacturaDao().insertarDetalles(detalles);

                runOnUiThread(() ->
                        NotificationHelper.enviarNotificacion(crearfactura.this, "Factura guardada", factura.codigoGeneracion)
                );

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(crearfactura.this, "Error al guardar factura", Toast.LENGTH_LONG).show()
                );
            }
        });
    }





}
