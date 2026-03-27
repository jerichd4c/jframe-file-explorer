package exploradorarchivos.core;

//Librerias necesarias
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.Base64;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

//Clase principal para ventana
public class FileExplorer extends  JFrame {
    //creacion de componente para ventanas emergentes
    private CardLayout cardLayout;
    //creacion de ventana principal
    private JPanel mainPanel;
    //creacion de variable para almacenar contador de palabras (se inserta dentro de panelStats)
    private JLabel etiquetaEstadistica;
    //booleano para verificar si el archivo esta modificado
    private boolean isModified;
    //variable para almacenar el archivo dentro del metodo
    private File currentFile;
    //creacion de la variable "areaTexto" dentro de la clase fileExplorer
    private JTextArea areaTexto;

    //metodo para inicializar la ventana con parametros default
    public FileExplorer(){
        //titulo del programa
        super("Explorador de Archivos");
        //metodo para cerrar la ventana al presionar la x (tambien se llama al metodo cerrar archivo)
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //se abrira una ventana nueva
        addWindowListener(new WindowAdapter() {
            //se implementa el metodo para cerrar la ventana a traves de override
            @Override
            //se invoca el metodo al cerrar la ventana (del programa)
            //e es para la (X)
            public void windowClosing(WindowEvent e) {
                //metodo fuera del file explorer, se usa el override para reconocerlo
                cerrarArchivo(); 
                //si no hay archivo Y no esta modificado, se cierra de forma normal
                if (currentFile == null && !isModified) {
                    dispose(); 
                }
            }
        });
        //metodo para ajustar la dimension de la ventana
        setSize(800, 600);
        //metodo para centrar la ventana
        setLocationRelativeTo(null);
        //metodo para hacer que la ventana siempre se muestre al inicio
        setVisible(true);
        //asignacion de variables layout y panel, con cardLayout se pueden manejar multiples 
        //ventanas con jpanel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        //creacion de botones editor y calculadora para cambiar de ventana (implementacion lista)
        mainPanel.add(crearPanelEditor(), "EDITOR");
        mainPanel.add(crearPanelCalculadora(), "CALCULADORA");
        
        //metodo para agregar el panel al menu principal
        add(mainPanel);
        
        //Barra de menu basica para navegar
        //creacion de barra de menu
        JMenuBar menuBar = new JMenuBar();
        //creacion del boton para la barra de menu
        JMenu menu = new JMenu("Navegacion");
        //popup menu para la barra de menu
        JMenuItem itemEditor = new JMenuItem("Editor");
        //accion para activar ventana de editor
        itemEditor.addActionListener(e -> cardLayout.show(mainPanel, "EDITOR"));
        //se agrega itemEditor a menu y se ejecuta cuando se activa addActionListener
        menu.add(itemEditor);
        //se agrega barra a menuBar
        menuBar.add(menu);
        //agrega barra a ventana principal
        setJMenuBar(menuBar);
    }

    //metodo para invocar UI (calculadora)
    private JPanel crearPanelCalculadora() {
        //creacion del popup con 3 filas y 2 columnas
        //ahora se usara borderLayout para que este en la parte superior y ocupe menos espacio
        JPanel panel = new JPanel(new BorderLayout(5,5));

        //los botones ahora se almacenan en una variable tipo flow, es decir en secuencia en vez de caja
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

        //creacion de variable local en metodo CrearPanelCalculadora para mostrar el resultado de la operacion
        JLabel resultadoLabel = new JLabel("Resultado: ", SwingConstants.CENTER);
        //se ajusta el tamaño del resultado para que este mas grande
        resultadoLabel.setPreferredSize(new Dimension(300, 30));

        //se almacena primer valor (visualizacion)
        JTextField texto1 = new JTextField(15);
        //se almacena segundo valor (visualizacion)
        JTextField texto2 = new JTextField(15);
    
        inputPanel.add(new JLabel("Primer valor: "));
        inputPanel.add(texto1);
        inputPanel.add(new JLabel("Segundo valor: "));
        inputPanel.add(texto2);
        
        //action listener generico para todas las operaciones
        //variable Action listener
        ActionListener operacionListener = e->{
            try {
                //se hace la operacion de sumar
                //convierte los numeros a enteros con parseInt
                double num1 = Double.parseDouble(texto1.getText());
                double num2 = Double.parseDouble(texto2.getText());
                //variable para manejar menu switch basandose en el input de la calculadora
                String operacion = ((JButton)e.getSource()).getText();
                //se guardara el resultado del switch en la operacion
                double resultado = switch(operacion) {
                    case "Sumar" -> num1 + num2;
                    case "Restar" -> num1 - num2;
                    case "Multiplicar" -> num1 * num2;
                    case "Dividir" -> {
                        //caso if si se divide entre 0
                        if (num2 == 0) throw new ArithmeticException("Division por cero");
                            //para el proceso de dividir entre 0 para evitar bucle
                            yield num1 / num2;
                        }
                    case "Potencia" -> Math.pow(num1, num2);
                        //en caso de que el input no sean numeros
                        default -> throw new IllegalArgumentException("Operacion invalida");
                    };
                
                //se muestra la operacion
                resultadoLabel.setText(String.format("Resultado: " + resultado));

            } catch (NumberFormatException ex) {
                //si hay un error se muestra un mensaje de error (entrada invalida)
                resultadoLabel.setText("Entrada invalida");
            } 
            
            catch (ArithmeticException ex) {
                //si hay un error aritmetico, se muestra mensaje de error (division por cero)
                resultadoLabel.setText("Error: "+ ex.getMessage());
            }
        };
        
        //creacion de panel para botones compacto, ahora no seran botones separados
        JPanel botonesPanel = new JPanel(new GridLayout(0, 3, 5, 5));
        //array con las operaciones disponibles
        String[] operaciones = {"Sumar", "Restar", "Multiplicar", "Dividir", "Potencia", "Borrar"};
        //operaciones se abreviara como op
        for (String op : operaciones) {
            //se va a crear el boton y se va a guardar los valores del array en dicho boton
            JButton boton = new JButton(op);
            //se le da el tamaño de 60x30
            boton.setPreferredSize(new Dimension(50,30));
            //si se quiere borrar los digitos, se activa con action listener
            if (op.equals("Borrar")) {
                //se activa el boton con actionListener
                boton.addActionListener(e->{
                    // se limpian los digitos con ""
                    texto1.setText("");
                    texto2.setText("");
                    resultadoLabel.setText("Resultado: ");
                });
                } else {
                    boton.addActionListener(operacionListener);
                }
                botonesPanel.add(boton);
            }

        //se agrega el panel de input a la ventana (donde se escribiran las operaciones)
        panel.add(inputPanel, BorderLayout.NORTH);
        //se agrega el panel de botones en la parte del centro
        panel.add(botonesPanel, BorderLayout.CENTER);
        //se agrega el panel de resultados en la parte de abajo
        panel.add(resultadoLabel, BorderLayout.SOUTH);

        //se retorna el panel
        return panel;
    }

    //metodo para invocar UI (escritura de texto)
    private JPanel crearPanelEditor () { 
        
        //creacion de panel dentro del menu principal para edicion de texto
        JPanel panel = new JPanel(new BorderLayout());
        areaTexto = new JTextArea();
        //panel de botones
        //variable para panel de botones (parte superior)
        JPanel panelBotones = new JPanel();
        JButton botonAbrir= new JButton("Abrir");  
        JButton botonGuardar= new JButton("Guardar");
        JButton botonCerrar= new JButton("Cerrar");

        //action listener para abrir el archivo con los botones de windows
        botonAbrir.addActionListener(e -> abrirArchivo());
        botonGuardar.addActionListener(e -> guardarArchivo());
        botonCerrar.addActionListener(e -> cerrarArchivo());

        //se agrega a la variable de botones
        panelBotones.add(botonAbrir);
        panelBotones.add(botonGuardar);
        panelBotones.add(botonCerrar);

        //variable para el boton de calculadora
        JButton botonCalculadora= new JButton("Calculadora");
        //listener para hacer que la ventana de calculadora se muestre al presionar el boton
        botonCalculadora.addActionListener(e -> cardLayout.show(mainPanel, "CALCULADORA"));
        //se agrega a la barra principal
        panelBotones.add(botonCalculadora);

        //centralizar el area del texto
        //ahora los botones estan en la parte superior y el area de texto en el centro
        panel.add(panelBotones, BorderLayout.NORTH);
        panel.add(new JScrollPane(areaTexto), BorderLayout.CENTER);
        //variable para almacenar estadisticas
        JPanel panelEstadisticas = new JPanel();   
        //se inicializa la etiqueta con variables defaults (0)
        etiquetaEstadistica = new JLabel("Caracteres: 0 | Palabras: 0 | Lineas: 0");
        //etiquetaEstadistica ahora es el argumento de panelEstadisticas (variable)
        panelEstadisticas.add(etiquetaEstadistica);
        //las estadisticas se mostran en la parte inferior
        panel.add(panelEstadisticas, BorderLayout.SOUTH);
        //metodo listener para actualizar actualizar los datos (escucha y responde)
        //getDocument obtiene el area de texto y addDocumentListener escucha el area de texto (creacion)
        areaTexto.getDocument().addDocumentListener(new DocumentListener() {
            //si se inserta o remueve se llama a el metodo actualizarEstadisticas y se ejecuta
            public void insertUpdate(DocumentEvent e) { 
                //actualizar isModified en el metodo crearPanelEditor (problema era que la variable no existia en el metodo)
                isModified = true;
                actualizarEstadisticas(); }
            public void removeUpdate(DocumentEvent e) { 
                isModified = true;
                actualizarEstadisticas(); }
            public void changedUpdate(DocumentEvent e) { 
                isModified = true;
                actualizarEstadisticas(); }
        });

        return panel;
    }

    //metodo para actualizar estadisticas
    private void actualizarEstadisticas() {
        //variable que almacena el texto leido
        String texto= areaTexto.getText();
        //variable que almacena el numero de caracteres
        int chars= texto.length();
        //variable cuya condicion es que registra palabra si y solo si hay un espacio entre los caracteres //s
        int words= texto.isEmpty() ? 0: texto.split("\\s+").length; 
        //variable que registra una linea si y solo si hay un salto //n (nueva logica)
        int lines=0;
        //si en el espacio NO hay texto, no se cuentan lineas
        if (!texto.isEmpty()) {
            //agrupa TODAS las lineas vacias en un array, \n es el salto para detectar la linea vacia
            String[] lineasArray = texto.split("\n");
            //bucle que recorre el array en todas las lineas vacias
            for (String linea : lineasArray) {
                //trim verifica si no hay espacios entre palabras y isempty verifica si esta vacio despues del trim
                if (!linea.trim().isEmpty()) {
                    //si la linea no esta vacia, se cuenta y se agrega al array
                    lines++;
                }
            }
        }
       
        //metodo que actualizara la etiqueta con los datos obtenidos (%d)
        etiquetaEstadistica.setText(String.format(
            "Caracteres: %d | Palabras: %d | Lineas: %d", chars, words, lines));
        }

    //metodo para abrir el archivo
    private void abrirArchivo() {
        //variable tipo JFileChooser para abrir archivos (usa directorio del usuario)
        JFileChooser fileChooser = new JFileChooser();

        //Opcional: quitar slashes si se quiere activar verificacion automatica

        //Filtro para que solo se puedan abrir archivos.txt
        //FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos TXT", "txt");
        //se escoge de filtro el filtro anteriormente creado
        //fileChooser.setFileFilter(filter);
        //todos los demas extensiones de archivos son bloqueadas
        //fileChooser.setAcceptAllFileFilterUsed(false);

        //condicional que tiene de argumento fileExplorer y se cumple si es una opcion valida (setter)
        if (fileChooser.showOpenDialog(this)== JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            //condicional que verifica y obtiene el nombre del archivo, lo pone en minusculas, y verifica si termina en .txt (SI NO SE CUMPLE UNA NO ES VALIDO)
            if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                //mensaje de error si no se cumple la condicion
                JOptionPane.showMessageDialog(this, "Solo se permiten archivos .txt", "Tipo de archivo invalido", JOptionPane.ERROR_MESSAGE);
                //regresa al inicio
                return;
            }
            //try-catch para verificar archivos con contrasena
            try {
               //variable para almacenar el contenido del archivo (usa el explorador de archivos para seleccionar el archivo)
               //selectedFile es la variable que almacena el archivo
               String contenido = new String(Files.readAllBytes(selectedFile.toPath()));
               //si el area del texto comienza con [PROTEGIDO] significa que el archivo esta encriptado
                if (contenido.startsWith("[PROTEGIDO]")) {
                String clave = JOptionPane.showInputDialog("Ingrese la contraseña:");
                //si no se ingresa ninguna contrasena, se regresa al inicio debido al fallo de verificacion
                if (clave == null || clave.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Contraseña no ingresada", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

            //try-catch para verificar la contrasena
                try {
                    contenido = descifrarContrasena(contenido.substring(11), clave); 

           } catch (Exception ex) { 
            //Error si el archivo que se abre tiene un formato invalido
            JOptionPane.showMessageDialog(this, "Contraseña incorrecta", "Error", JOptionPane.ERROR_MESSAGE);
            return;
           }
        }
        //ya el archivo esta abierto, por lo tanto se pone en el area de texto el cotenido
        areaTexto.setText(contenido);  
        //el archivo selecionado sera el de la clase actual (FileExplorer) 
        currentFile = selectedFile;
        //variable isModified que se actualizara en el metodo de estadisticas
        isModified= false;
        actualizarEstadisticas();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
            "Error al leer el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

    //metodo para guardar el archivo
    private void guardarArchivo(){
        //variable para guardar el archivo
        JFileChooser fileChooser = new JFileChooser();
        //filtro que solo permite archivos .TXT
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos TXT", "txt"));
        //el archivo selecionado sera el de la clase actual (FileExplorer) (setter)
        int result = fileChooser.showSaveDialog(this);
        //condicional de que si se cumple, el archivo se guarda en la variable
        //primer condicional
        if (result == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            //condicional que expresa si el archivo no es un .txt, retorna la ruta absoluta del archivo y le agrega .txt
            //segundo condicional
            if (!archivo.getName().toLowerCase().endsWith(".txt")) {
                archivo = new File(archivo.getAbsolutePath() + ".txt");
            }
            // confirmar sobreescritura si existe un archivo con el mismo nombre
            if (archivo.exists()) {
                int confirmacion = JOptionPane.showConfirmDialog(this,
                    "El archivo ya existe. ¿Desea sobreescribirlo?",
                    "Confirmar", 
                    // se le dara la opcion al usuario de escoger SI o NO
                    JOptionPane.YES_NO_OPTION
                    );
                    //si no es ninguna de las dos, cancelar
                    if (confirmacion != JOptionPane.YES_OPTION) return;
                    }
                    //popup que aparecera al momento de guardar
                    int opcion = JOptionPane.showConfirmDialog(this,
                        "¿Desea proteger el archivo con contraseña?",
                        "Proteger con contraseña",
                        JOptionPane.YES_NO_OPTION);
                    //variable que almacenara el contenido y la contrasena (si es que tiene)
                    String contenido= areaTexto.getText();

                    if (opcion== JOptionPane.YES_OPTION) {
                        String clave = JOptionPane.showInputDialog(this, "Ingrese la contraseña:");
                        //si la contrasena es nula o vacia, sigue el flujo normal
                        if (clave == null || clave.isEmpty()) return;
                        //el archivo ahora se guardar como una variable en vez de como areaTexto
                        contenido= "[PROTEGIDO]"+cifrarContrasena(contenido, clave);
                    }

                    try (FileWriter writer = new FileWriter(archivo)) {
                        writer.write(contenido);
                        currentFile = archivo;
                        isModified = false; 
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Error al guardar el archivo", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}        
    //metodo para cerrar el archivo por medio del boton
    //cambiado a boolean para que devuelva verdadero o falso
    public boolean cerrarArchivo() {
        //condicional que verifica si el archivo se modifico
        if (isModified) {
            //se muestra el dialogo de confirmacion
            int opcion = mostrarDialogoGuardar();
            //menu switch para las opciones tomando de base mostrarDialogo guardar
            switch (opcion) {
                case 0: //Opcion SI
                    guardarArchivo();
                    if (isModified) return false; //si se cancelo el guardado, regresa al inicio
                    break;
                case 1: //Opcion NO
                    isModified=false;
                    break; //se cierra el archivo
                case 2: //Cancelar
                default:
                    return false; //no se cierra ni se guarda, se sigue en la ventana
            }
        }
       //limpia el area de texto en su totalidad
       areaTexto.setText("");
       //la variable que contenia el espacio de memoria del archivo se declara vacia
       currentFile = null;
       //estado de isModified se declara falso en caso de que no entre en el condicional
       isModified = false;
       //se actualiza las estadisticas en caso de que se haya cerrado el archivo
       actualizarEstadisticas();
       return true;
    }

    //metodo para confirmar guardado
    public int mostrarDialogoGuardar() {
        //toma de argumento el archivo actual "this" y muestra las opciones disponibles (message, title, optionType, messageType
        //icon, options, initialValue)
        return JOptionPane.showOptionDialog (this,
        //mensaje inicial
            "¿Desea guardar los cambios antes de cerrar?",
        //titulo del popup
            "Confirmar cierre",
            //se le dara la opcion al usuario de escoger SI, NO o CANCELAR (tipo de opcion)
            JOptionPane.YES_NO_CANCEL_OPTION,
            //tipo de mensaje
            JOptionPane.WARNING_MESSAGE,
            //no se va a mostrar ningun icono (parametro de icono)
            null, 
            //parametros de las opciones disponibles
            new Object[]{"SI", "NO", "CANCELAR"},
            //seleccion por defecto
            "Guardar cambios"
            );
        }


    //PARA ESTOS DOS METODOS SE UTILIZARA EL VALOR DECIMAL DE LOS CARACTERES

    //metodo para cifrar contraseñas
    private static String cifrarContrasena(String texto, String clave) {
        //stringbuilder permite modificar el texto sin necesidad de crear otro objeto adicional
        StringBuilder cifrado = new StringBuilder();
        //el texto se convierte a un valor int sumando TODO el string y la suma se guarda en la variable
        int sumClave= clave.chars().sum();
        //para cada caracter del array, convertira cada dato a un char y se le sumara a el array
        for (char c : texto.toCharArray()) {
            //bucle que va a agregar los datos convertidos a char a el array StringBuilder, sumando el valor del char Y el valor total de la suma del string
            cifrado.append((char) (c + sumClave));
        }
        //metodo getEncoder retornara una contrasena cifrada
        //metodo getBytes retorna el objeto tipo String en un arreglo, y toString lo convierte a un string de bytes legible (no se va a mostrar)
        return Base64.getEncoder().encodeToString(cifrado.toString().getBytes());

        /////ejemplo practico:
        // cifrado.toString() → "×˜˜"
        // .getBytes() → [0xc3, 0x97, 0xc3, 0x9f, 0xc2, 0x81, 0xc2, 0x8d, 0xc2, 0x90, 0xc2, 0x8f, 0xc2, 0x8d, 0xc2, 0x90, 0xc2, 0x8e, 0xc2, 0x8f] (byte array)
        // Base64.getEncoder() → Base64.Encoder object
        // .encodeToString() → "eHR5IG1vZGUgdGhlIG1lc3NhZ2U=" (Base64-encoded string)
        // return → "eHR5IG1vZGUgdGhlIG1lc3NhZ2U=" (final result)
    }

    //metodo para descifrar contraseñas
    private static String descifrarContrasena(String textoCifrado, String clave) {
        //arreglo de bites, se hace la llamada al metodo decode que va a decodificar el texto cifrado
        //getDecoder va a retornar el la contrasena decodificada
        byte[] bytesDescifrados = Base64.getDecoder().decode(textoCifrado); 
        //variable que almacenara la contrasena descifrada
        String texto = new String(bytesDescifrados);
        //se aplicara la misma logica para cifrar pero a la inversa
        //varible que StringBuilder que retornara la contrasena original
        StringBuilder original = new StringBuilder();
        //el texto se convierte a un valor int sumando TODO el string y la suma se guarda en la variable 
        int sumClave= clave.chars().sum();
        //para cada caracter del array, convertira cada dato a un char y se le restara a el array
        for (char c : texto.toCharArray()) {
            //bucle que va a agregar los datos convertidos a char a el array StringBuilder, RESTANDO el valor del char Y el valor total de la suma del string
            //mientras la resta este dentro del rango valido de la suma de la clave, significa que el proceso de encriptacion fue correcto
            original.append((char) (c - sumClave));
        }
        //se retorna la contrasena original
        return original.toString();
    }

    //ejecucion main
    public static void main(String[] args) {
        //execucion del metodo para creacion de ventana
        SwingUtilities.invokeLater(() -> {
            FileExplorer frame= new FileExplorer();
            frame.setVisible(true);
        });
    }
}