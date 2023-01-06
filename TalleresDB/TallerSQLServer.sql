
DECLARE @N INT
SET @N = 1
WHILE @N < 100
    BEGIN
        IF (@N % 2) = 0
            SELECT @N AS NUMERO , 'PAR'
        ELSE
            SELECT @N AS NUMERO , 'IMPAR'
        SET @N = @N + 1

END

DECLARE
    @i INT,
    @j INT,
    @count INT

SET @i = 1
WHILE (@i < = 100)
    BEGIN
        SET @count = 0
        SET @j = 1
        WHILE (@j < = @i)
            BEGIN
                IF (@i % @j = 0) 
                    SET @count = @count + 1
                SET @j = @j + 1
            END
        IF (@count = 2) 
            PRINT @i
        SET @i = @i + 1
    END

    
alter table ENTREGA_ELEMENTOS
add DURACION int default 0;

UPDATE ENTREGA_ELEMENTOS set DURACION = 30;

alter table EMPLEADOS
add CLAVE VARCHAR(250) default '---';

create procedure PARES
  as 
  declare @N INT=0
while (@N<100)
begin
     set @N=@N+1
if (@N % 2 =0)
          Print 'Valor N: ' + Cast(@N as Char(3))
end;


CREATE procedure primos
as 
begin
declare @number INT = 100
declare @i int
declare @j int
declare @isPrime int
set @isPrime=1
set @i=2
set @j=2
while(@i<=@number)
begin
    while(@j<=@number)
    begin
        if((@i<>@j) and (@i%@j=0))
        begin
            set @isPrime=0
            break
        end
        else
        begin
            set @j=@j+1
        end
    end
    if(@isPrime=1)
    begin
        SELECT @i
    end
    set @isPrime=1
    set @i=@i+1
    set @j=2
end
end


create procedure comparar_numeros (@num1 int,@num2 int)
  as 
begin

	if (@num1 > @num2)
	Print 'El numero: ' + Cast(@num1 as Char(3)) + ' es mayor que ' + Cast(@num2 as Char(3)) 
	if (@num1 < @num2)
	Print 'El numero: ' + Cast(@num1 as Char(3)) + ' es menor que ' + Cast(@num2 as Char(3)) 
	if (@num1 = @num2)
	Print 'Los numeros son iguales'
end;


CREATE FUNCTION promedio (@cod int) RETURNS INT
AS 
BEGIN
DECLARE @Promedio int = 0
SELECT @Promedio =  AVG(de.v_unitario) 
FROM elementos e
INNER JOIN detalle_entrada de
ON de.elemento = @cod
WHERE e.usos like 'PROTE%' or e.usos like '%PROTE%';
RETURN @Promedio
END


CREATE FUNCTION entregado_todos (@ident varchar(10)) RETURNS INT
AS 
BEGIN
DECLARE @cantidad int = 0
DECLARE @retorno int = 0
SELECT @cantidad =  COUNT(elem.elemento) 
FROM empleados e
INNER JOIN ELEMENTOS_ASIGNADOS ele
ON e.IDENTIFICACION = @ident
INNER JOIN elementos elem
ON elem.CODIGO = ele.ELEMENTO
WHERE ele.ACTUAL = 'N' and elem.USOS like '%PROTE%' or elem.usos like 'PROTE%';
if (@cantidad > 0)
	set @retorno = 1

return @retorno;
END


SELECT e.IDENTIFICACION, e.NOMBRE_1+' '+e.APELLIDO_1 +' '+e.APELLIDO_2 as nombre, c.CARGO, asig.ACTUAL as entregado
FROM empleados e
INNER JOIN HISTORIAL_LABORAL hl
ON hl.EMPLEADO = e.IDENTIFICACION
INNER JOIN CARGOS c
ON c.COD_CARGO = hl.CARGO
INNER JOIN ELEMENTOS_ASIGNADOS asig
ON asig.EMPLEADO = e.IDENTIFICACION
INNER JOIN ELEMENTOS ele
ON ele.CODIGO = asig.ELEMENTO
WHERE ele.USOS like '%PROTE%' or ele.USOS like 'PROTE%'


CREATE FUNCTION esta_matriculado (@codigo int, @periodo int,@ano int) RETURNS INT
AS 
BEGIN
DECLARE @matriculado int = 0
DECLARE @retorno bit = 0
SELECT @matriculado =  COUNT (i.ESTUDIANTE)
FROM INSCRIPCIONES i
WHERE i.PERIODO = @periodo AND i.ANO = @ano AND i.ESTUDIANTE = @codigo;
if (@matriculado > 0)
	set @retorno = 1
return @retorno;
END


CREATE FUNCTION ano_sqlserver() RETURNS INT
AS 
BEGIN
DECLARE @ano int = 0
SELECT @ano =  YEAR(GETDATE());
RETURN @ano
END


CREATE FUNCTION semestre_ano() RETURNS INT
AS 
BEGIN
DECLARE @ano int = 0
DECLARE @semestre int = 1
SELECT @ano =  YEAR(GETDATE());
if (@ano > 6)
	set @semestre = 2
RETURN @semestre
END


CREATE FUNCTION promedio_est (@micodigo int) RETURNS INT
AS 
BEGIN
DECLARE @nota int = 0

SELECT @nota =  ROUND(AVG(I.NOTA),2)
FROM INSCRIPCIONES i
WHERE i.ESTUDIANTE = @micodigo

return @nota;
END


CREATE FUNCTION num_estudiantes (@cod_matera int, @cod_programa int, @cod_facul int, @ano int,@periodo int ) RETURNS INT
AS 
BEGIN
DECLARE @cant int = 0

SELECT @cant =  COUNT(i.ESTUDIANTE)
FROM INSCRIPCIONES i
WHERE i.materia = @cod_matera AND i.PROGRAMA = @cod_programa AND i.FACULTAD = @cod_facul  AND i.ANO = @ano AND i.PERIODO = @periodo;

return @cant;
END


