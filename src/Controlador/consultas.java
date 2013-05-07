package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.pool.OracleDataSource;


public class consultas {
    
    public String prueba () throws ClassNotFoundException{
    String resultado="";
        try {
           
          Class.forName("oracle.jdbc.driver.OracleDriver");
          // Set the connection properties.
          // required: SYSDBA
          Properties prop = new Properties();
          prop.setProperty("user","sys");
          prop.setProperty("password","ama");
          prop.setProperty("internal_logon","sysdba");

          OracleDataSource ods = new OracleDataSource();
          ods.setConnectionProperties(prop);
          ods.setURL("jdbc:oracle:thin:@127.1.0.0:1521:xe");//
          OracleConnection ocon = (OracleConnection)ods.getConnection();
          System.out.println("Connected");     

          // shutdown the database
          ocon.shutdown(OracleConnection.DatabaseShutdownMode.IMMEDIATE);
          System.out.println("Instance stopped");
          Statement stmt = ocon.createStatement();
          stmt.execute("alter database close normal");
          stmt.execute("alter database dismount");
          stmt.close();
          System.out.println("Database closed and dismounted");
          ocon.shutdown(OracleConnection.DatabaseShutdownMode.FINAL);
          ocon.close();
          ods.close();
          System.out.println("Database stopped");

        } catch(SQLException e) {
          System.out.println(e.getMessage());
        }
    return resultado;
    }
    
    public String consultas (int opc){
    String resultado="";
    
    try {

		/**** Cargamos el driver ****/
		Class.forName("oracle.jdbc.driver.OracleDriver");
		System.out.println( "Fino ps");
		/**** Realizamos la conexión ****/
		Connection con = DriverManager.getConnection
                ("jdbc:oracle:thin:@127.1.0.0:1521:xe", "SYS AS SYSDBA", "ama");

		/**** avisamos ****/
		System.out.println( "Si he llegado hasta aquí es que se ha producido la conexión");
		System.out.println( "Si no se hubiera producido, se habría disparado SQLException");

                   /*Para realizar una consulta*/
           int x=1;
                  
           switch(opc){
               case 1:
                    System.out.println("1a. Nombre de las tablas de la BD-----------------------------------------");
                    Statement stmt = con.createStatement();
                    ResultSet rset = stmt.executeQuery("select * from DBA_tables ");//"select * from producto order by precio ASC");
                    //while (rset.next()){ System.out.println(rset.getString("table_name")+" "+x); x++;}
                    //ejemplo de esta consulta: HR JOBS
                    while (rset.next()){ resultado=resultado+rset.getString(2)+"\n";}
               break;
               case 2: 
                   
                   System.out.println("1b. Nombre de los indices de la BD-----------------------------------------");
                    //Para calcular el indice usar el peor caso log 2 (TA)*VT
                    Statement stmt1b = con.createStatement();
                    ResultSet rset1b = stmt1b.executeQuery("select * from DBA_indexes");
                    while (rset1b.next()){ resultado=resultado+rset1b.getString(2)+ " " +x+"\n";x++; }
               break;
               case 3: 
                System.out.println("2a. Cant de tablas --------------------------------------------------------");
                Statement stmt2 = con.createStatement();
                ResultSet rset2 = stmt2.executeQuery("select count(*), OWNER from DBA_TABLES group by OWNER");//"select * from producto order by precio ASC");
               while (rset2.next()){ resultado=resultado+rset2.getString(1)+ " " + rset2.getString(2)+"\n";}
               break;
               case 4: 
                System.out.println("2b. Cant de indices por tablas ---------------------------------------------");
                Statement stmt2b = con.createStatement();
                ResultSet rset2b = stmt2b.executeQuery("select TABLE_OWNER, TABLE_NAME, count(*) from dba_ind_columns where COLUMN_POSITION=1 group by TABLE_OWNER, TABLE_NAME having  count(*) > 1");//"select * from producto order by precio ASC");
                 while (rset2b.next()){ resultado=resultado+rset2b.getString(2)+ " " + rset2b.getString(3)+"\n";}
               break;
               case 5: 
                   System.out.println("3. Tamaño de las tablas en bloque ------------------------------------------");
                    Statement stmt3 = con.createStatement();
                    ResultSet rset3 = stmt3.executeQuery("select segment_name, blocks from dba_segments where segment_type='TABLE'");//"select * from producto order by precio ASC");
                    while (rset3.next()){ resultado=resultado+rset3.getString(1)+ " " + rset3.getString(2)+"\n";}
               break;
               case 6: 
                   System.out.println("4. Tamaño de cada registro (en bytes)--------------------------------------------------");
                    Statement stmt4 = con.createStatement();
                    ResultSet rset4 = stmt4.executeQuery("select owner,segment_name,sum((bytes/1024/1024)) Bytes from sys.dba_extents where segment_type='TABLE' group by owner,segment_name order by owner,segment_name, bytes");
                    while (rset4.next()){ resultado=resultado+rset4.getNString(1)+ " "+ rset4.getString(2)+ " " + rset4.getString(3)+"\n";}
               break;
               case 7:
                    System.out.println("5. El tamaño de cada columna (en bytes)");
          
            Statement stmt5 = con.createStatement();
            ResultSet rset5 = stmt5.executeQuery("SELECT column_name, data_type, data_length FROM all_tab_columns");
            while (rset5.next()){ 
            resultado=resultado+rset5.getString(1)+" "+rset5.getString(2)+" "+ rset5.getString(3)+"\n";
            System.out.println(rset5.getString(1)+ " " + rset5.getString(2)+ " " + rset5.getString(3));
            }
               break;
               case 8: 
                   System.out.println("6. Fdb de las tablas e índices (este valor lo debe calcular el sistema, se asume registros fijos no extensibles)");
          
               String result6=""; double bloque=0;
               Statement stmt6 = con.createStatement(); //tamaño bloque
               ResultSet rset6 = stmt6.executeQuery("select SUM(blocks) TotalDeBloq from dba_segments where segment_type='TABLE'");
               while (rset6.next()){ result6=rset6.getString(1); }
               bloque = Double.parseDouble(result6);
                    
               String result6b=""; double registro=0, fb=0;
               Statement stmt6b = con.createStatement(); //tamaño registro
               ResultSet rset6b = stmt6b.executeQuery("select SUM(sum((bytes/1024/1024))) Bytes from sys.dba_extents where segment_type='TABLE' group by owner,segment_name order by owner,segment_name, bytes");
               while (rset6b.next()){ result6b = rset6b.getString(1);}
               registro = Double.parseDouble(result6b);
            
               fb=bloque/registro;
               //System.out.println("FB\n"+fb);
               resultado=""+fb;
               break;
               case 9: 
                   System.out.println(" 7. cambiar archivelog");
          
          String log="";
          Statement stmt8 = con.createStatement();
          ResultSet rset8 = stmt8.executeQuery("select LOG_MODE from SYS.V$DATABASE");//"select * from producto order by precio ASC");
          while (rset8.next()){ log=rset8.getString(1);}
          
          if(log.compareTo("NOARCHIVELOG")==0){
              //cambiar a si archivelog
           Statement stmt8a = con.createStatement();
           System.out.println("XD 1");
           ResultSet rset8a = stmt8a.executeQuery("alter system set log_archive_dest_1='location=C:archive_log_offline' scope=spfile");//"select * from producto order by precio ASC");
          System.out.println("XD 2");
          //Statement stmt8a1 = con.createStatement();
          
          Statement stmt8a1 = con.createStatement();
          ResultSet rset8a1 = stmt8a1.executeQuery("shutdown immediate");//"select * from producto order by precio ASC");
          System.out.println("XD 2");
          //Statement stmt8a1 = con.createStatement();shutdown immediate
          Statement stmt8a2 = con.createStatement();
          ResultSet rset8a2 = stmt8a2.executeQuery("startup mount");
              System.out.println("XD 4");
          Statement stmt8a3 = con.createStatement();
          ResultSet rset8a3 = stmt8a3.executeQuery("alter database archivelog;");
          System.out.println("XD 4");
           Statement stmt8a4 = con.createStatement();
          ResultSet rset8a4 = stmt8a4.executeQuery("alter database open;");
          System.out.println("XD 5");
          Statement stmt8a5 = con.createStatement();
          ResultSet rset8a5 = stmt8a5.executeQuery("select LOG_MODE from SYS.V$DATABASE");//"select * from producto order by precio ASC");
          while (rset8a4.next()){ resultado=log=rset8a4.getString(1);}
          
          System.out.println("XD "+log);
          }else{
          // cambiar a no archivelog
          }
               break;
               case 10: 
               break;
               default:
               break;
           }     
           
		/**** Una buena costumbre: cerramos la conexión ****/
		con.close();
		}
		/**** Excepción que se dispara si falla la carga del driver ****/
		catch( ClassNotFoundException e ) {e.printStackTrace();}

		/**** Excepción que se dispara si falla la conexión *****/
		catch ( SQLException e) {e.printStackTrace();}
    return resultado;
    }
}
