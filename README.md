CREATE TABLE Usuarios_Enfermeros(
id_usuario INT PRIMARY KEY,
correo_usuario VARCHAR2(100) UNIQUE NOT NULL,
contrasena VARCHAR2(50) NOT NULL
);

INSERT ALL
INTO Usuarios_Enfermeros (correo_usuario, contrasena) VALUES ('diana@gmail.com', 'Diana#2006')
INTO Usuarios_Enfermeros (correo_usuario, contrasena) VALUES ('ricardo@gmail.com', 'Ricky#123')
SELECT * FROM dual;
SELECT * FROM Usuarios_Enfermeros;

CREATE SEQUENCE sec_usuarios_enfermeros
START WITH 1
INCREMENT BY 1;

CREATE TRIGGER TrigUsuariosEnfermeros
BEFORE INSERT ON Usuarios_Enfermeros
FOR EACH ROW
BEGIN
SELECT sec_usuarios_enfermeros.NEXTVAL INTO : NEW.id_usuario
FROM DUAL;
END;

CREATE TABLE Pacientes(
id_paciente INT PRIMARY KEY,
nombres VARCHAR2(100) NOT NULL,
apellidos VARCHAR2(100) NOT NULL,
edad INT NOT NULL,
enfermaedad VARCHAR2(200) NOT NULL,
numero_habitacion INT NOT NULL,
numero_cama INT NOT NULL,
fecha_ingreso DATE NOT NULL
);

commit;

CREATE SEQUENCE sec_pacientes
START WITH 1
INCREMENT BY 1;

CREATE TRIGGER TrigPacientes
BEFORE INSERT ON Pacientes
FOR EACH ROW
BEGIN
SELECT sec_pacientes.NEXTVAL INTO : NEW.id_paciente
FROM DUAL;
END;

INSERT ALL
INTO Pacientes(nombres, apellidos, edad, enfermaedad, numero_habitacion, numero_cama, fecha_ingreso) VALUES ('Juan', 'Pérez', 45, 'Diabetes', 101, 1, '19/07/2024')
INTO Pacientes(nombres, apellidos, edad, enfermaedad, numero_habitacion, numero_cama, fecha_ingreso) VALUES ('Ana', 'García', 30, 'Hipertensión', 102, 2, '20/07/2024')
SELECT * FROM dual;
SELECT * FROM Pacientes;

CREATE TABLE Medicamentos(
id_medicamento INT PRIMARY KEY,
nombre_medicamento VARCHAR2(100) NOT NULL,
descripcion VARCHAR2(500) NOT NULL
);

CREATE SEQUENCE sec_medicamentos
START WITH 1
INCREMENT BY 1;

CREATE TRIGGER TrigMedicamentos
BEFORE INSERT ON Medicamentos
FOR EACH ROW
BEGIN
SELECT sec_medicamentos.NEXTVAL INTO : NEW.id_medicamento
FROM DUAL;
END;

INSERT ALL
INTO Medicamentos (nombre_medicamento, descripcion) VALUES ('Insulina', 'Medicamento utilizado para el tratamiento de la diabetes.')
INTO Medicamentos (nombre_medicamento, descripcion) VALUES ('lisinopril', 'Medicamento utilizado para el tratamiento de la hipertensión.')
SELECT * FROM dual;
SELECT * FROM Medicamentos;

CREATE TABLE Aplicacion_Medicamentos(
id_aplicacion INT PRIMARY KEY,
hora_aplicaciones VARCHAR2(10) NOT NULL,
id_paciente INT,
id_medicamento INT,
CONSTRAINT fk_paciente
FOREIGN KEY (id_paciente) REFERENCES Pacientes(id_paciente),
CONSTRAINT fk_medicamento
FOREIGN KEY (id_medicamento) REFERENCES Medicamentos(id_medicamento)
);

SELECT id_paciente FROM Pacientes WHERE (nombres || ' ' || apellidos) = 'Juan Pérez';
SELECT id_medicamento FROM Medicamentos WHERE nombre_medicamento = 'Insulina';

SELECT * FROM Pacientes;

CREATE SEQUENCE sec_aplicacion_medicamentos
START WITH 1
INCREMENT BY 1;

CREATE TRIGGER TrigAplicacionMedicamentos
BEFORE INSERT ON Aplicacion_Medicamentos
FOR EACH ROW
BEGIN
SELECT sec_aplicacion_medicamentos.NEXTVAL INTO : NEW.id_aplicacion
FROM DUAL;
END;

INSERT ALL
INTO Aplicacion_Medicamentos (hora_aplicaciones, id_paciente, id_medicamento) VALUES ('6:00 P.M.', 1, 1)
INTO Aplicacion_Medicamentos (hora_aplicaciones, id_paciente, id_medicamento) VALUES ('9:00 A.M.', 2, 2)
SELECT * FROM dual;
SELECT * FROM Aplicacion_Medicamentos;

DESCRIBE Aplicacion_Medicamentos;

SELECT 
    p.nombres AS Nombres,
    p.apellidos AS Apellidos,
    p.edad AS Edad,
    p.enfermaedad AS Enfermedad,
    p.numero_habitacion AS Numero_Habitacion,
    p.numero_cama AS Numero_Cama,
    p.fecha_ingreso AS Ingreso,
    m.nombre_medicamento AS Medicamentos,
    am.hora_aplicaciones
FROM 
    Aplicacion_Medicamentos am
INNER JOIN 
    Pacientes p ON am.id_paciente = p.id_paciente
INNER JOIN 
    Medicamentos m ON am.id_medicamento = m.id_medicamento;
