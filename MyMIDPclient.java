//INPUT FORM .java


import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import javax.microedition.rms.RecordStore;

public class MyMIDPclient extends MIDlet implements CommandListener, Runnable {

    private Display display; // Reference to Display object

    private Form fmMain;         // The main form
    private Alert alError;       // Alert to error message
    private Command cmModify;
    private Command cmDelete;
    private Command cmExit;
    private Command cmSubmit;
    private Command cmBack;
    private Command cmInsert;
    private Command cmSync;
    private Command Sync;
    private Command cmOK;
    private Command cmDone;
    private Command cmRead;

    //RECORD STORE DETAILS
    private RecordStore rs = null;
    static final String REC_STORE = "RECORDSTORE";
    String s;
    //INPUT DETAILS
    private TextField tfData;    // set data
    private TextField tfRecID;
    private TextField tfcliID; // Get id
    private Form form1;
    private Form form2;
    private Form form3;
    private Form form4;

    String errorMsg;

    public MyMIDPclient() {
        display = Display.getDisplay(this);

        // CREATE COMMANDS
        cmInsert = new Command("Insert", Command.ITEM, 2);
        cmDelete = new Command("Delete", Command.ITEM, 3);
        cmModify = new Command("Modify", Command.ITEM, 4);
        cmRead = new Command("show records", Command.SCREEN, 6);
        cmSync = new Command("Sync", Command.ITEM, 5);
        Sync = new Command("Sync", Command.ITEM, 7);

        cmSubmit = new Command("Submit", Command.SCREEN, 0);
        cmOK = new Command("OK", Command.SCREEN, 0);
        cmDone = new Command("DONE", Command.SCREEN, 0);
        cmExit = new Command("BACK", Command.BACK, 1);

        // Create Form, add commands & componenets, listen for events
        fmMain = new Form("MENU");

        fmMain.addCommand(cmExit);
        fmMain.addCommand(cmModify);
        fmMain.addCommand(cmDelete);
        fmMain.addCommand(cmInsert);
        fmMain.addCommand(cmSync);
        fmMain.addCommand(cmRead);
        fmMain.setCommandListener(this);

        //CREATE INSERT FORM
        form1 = new Form("Insert form");
        form1.addCommand(cmExit);
        form1.addCommand(cmSubmit);
        form1.setCommandListener(this);
        //CREATE MODIFY FORM
        form2 = new Form("Modify form");
        form2.addCommand(cmExit);
        form2.addCommand(cmOK);
        form2.setCommandListener(this);
        //CREATE DELETE FORM
        form3 = new Form("Delete form");
        form3.addCommand(cmExit);
        form3.addCommand(cmDone);
        form3.setCommandListener(this);

        form4 = new Form("client id form");
        form4.addCommand(cmExit);
        form4.addCommand(Sync);
        form4.setCommandListener(this);

    }

    public void startApp() {
        display.setCurrent(fmMain);

    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    //RECORD STORE OPEN N CLOSE
    public void openRecStore() {
        try {
            rs = RecordStore.openRecordStore(REC_STORE, true);
        } catch (Exception e) {
        }
    }

    public void closeRecStore() {
        try {
            rs.closeRecordStore();
        } catch (Exception e) {
        }
    }

    public void InsertRecord(String str) {
        byte[] rec = str.getBytes();
        try {
            rs.addRecord(rec, 0, rec.length);
            System.out.println("inserted" + str);
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    public void commandAction(Command c, Displayable st) {

        openRecStore();
        if (c == cmInsert) {

            tfData = new TextField("Data : ", "", 10, TextField.ANY);
            form1.append(tfData);
            display.setCurrent(form1);

        } else if (c == cmModify) {

            tfRecID = new TextField("RecordId: ", "", 5, TextField.NUMERIC);
            tfData = new TextField("Data: ", "", 10, TextField.ANY);

            System.out.println("modifiying");
            form2.append(tfRecID);
            form2.append(tfData);
            display.setCurrent(form2);
        } else if (c == cmDelete) {

            tfRecID = new TextField("RecordId: ", "", 5, TextField.NUMERIC);

            System.out.println("deleting ");
            form3.append(tfRecID);
            display.setCurrent(form3);
        } else if (c == cmDone) {

            DeleteRecord(tfRecID.getString());// tfcliID.getString();
            System.out.println("deleted");
            readRecords();

        } else if (c == cmRead) {

            readRecords_a();

        } else if (c == cmExit) {
            destroyApp(false);
            notifyDestroyed();
        } else if (c == cmSubmit) {

            s = tfData.getString();

            InsertRecord(s);
            System.out.println("submitted");
            try {
                readRecords();
            } catch (Exception e) {
                System.out.println("submit failed");
            }

        } else if (c == cmOK) {

            ModifyRecord(tfData.getString(), tfRecID.getString());
            System.out.println("modified");
            readRecords();

        } else if (c == cmSync) {

            tfcliID = new TextField("clientId: ", "", 5, TextField.NUMERIC);

            form4.append(tfcliID);
            display.setCurrent(form4);
            System.out.println("client form called");

        } else if (c == Sync) {

            Thread t = new Thread(this);
            t.start();
            System.out.println("threads started");

        }

    }

    public void ModifyRecord(String str, String i) {
        byte[] rec = str.getBytes();
        try {
            System.out.println("modifying ");
            rs.setRecord(Integer.parseInt(i), rec, 0, rec.length);
        } catch (Exception e) {
        }
    }

    public void DeleteRecord(String i) {
        try {
            rs.deleteRecord(Integer.parseInt(i));
        } catch (Exception e) {
        }
    }

    public void readRecords() {
        try {
            byte[] recData = new byte[5];
            int len;

            Form form = new Form("Display all records");
            for (int i = 1; i <= rs.getNumRecords(); i++) {
                if (rs.getRecordSize(i) > recData.length) {
                    recData = new byte[rs.getRecordSize(i)];
                }
                len = rs.getRecord(i, recData, 0);
                System.out.println("------------------------------");
                System.out.println("Record " + i + " : " + new String(recData, 0, len));
                System.out.println("------------------------------");

                form.append("Record " + i + " : " + new String(recData, 0, len) + "\n");
                System.out.println("reading done");

                display.setCurrent(form);

            }
        } catch (Exception e) {
        }
    }

    public void readRecords_a() {
        try {
            byte[] recData = new byte[5];
            int len;

            Form form = new Form("Display all records");
            for (int i = 1; i <= rs.getNumRecords(); i++) {
                if (rs.getRecordSize(i) > recData.length) {
                    recData = new byte[rs.getRecordSize(i)];
                }
                len = rs.getRecord(i, recData, 0);
                System.out.println("------------------------------");
                System.out.println("Record " + i + " : " + new String(recData, 0, len));
                System.out.println("------------------------------");

                form.append("Record " + i + " : " + new String(recData, 0, len) + "\n");
                System.out.println("reading done again");

                display.setCurrent(form);

            }
        } catch (Exception e) {
        }

    }

    public void run() {
        try {
            syncRecords();
            System.out.println("SYNCHRONISING RECORDS");
        } catch (IOException ex) {
            System.out.println("error in run");
        }
    }

    public void syncRecords() throws IOException {
        storeRecords_withPOST();

    }

    private void showAlert(String msg) {
        // Create Alert, use message returned from servlet
        alError = new Alert("Error", msg, null, AlertType.ERROR);

        // Set Alert to type Modal
        alError.setTimeout(Alert.FOREVER);

        // Display the Alert. Once dismissed, display the form
        display.setCurrent(alError, fmMain);
    }

    private void storeRecords_withPOST() throws IOException {

        HttpConnection http = null;
        OutputStream oStrm = null;
        InputStream iStrm = null;
        boolean ret = false;
        StringBuffer sb = new StringBuffer();
        String url = "http://localhost:8080/serveletdatabse/NewServlet";

        try {

            //HTTP CONNECTION DETAILS
            http = (HttpConnection) Connector.open(url);
            http.setRequestMethod(http.POST);
            http.setRequestProperty("IF-Modified-Since", "25 Nov 2001 15:17:19 GMT");
            http.setRequestProperty("User-Agent", "Profile/MIDP-1.0 Configuration/CLDC-1.0");
            http.setRequestProperty("Content-Language", "en-CA");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            System.out.println("website connected");

            //WRITE DATA TO STREAM
            DataOutputStream dataOutStream = http.openDataOutputStream();
            String str = ",";
            //dataOutStream.write(cliId.getBytes());
            // dataOutStream.write(str.getBytes());

            //READ FROM RECORDSTORE
            openRecStore();

            // GENERATE CLIENT LOCAL TIMESTAMP
            Date d;
            long l1 = new Date().getTime();
            byte[] CT1 = Long.toString(l1).getBytes();
            dataOutStream.write(CT1);
            dataOutStream.write(str.getBytes());
            long l2 = rs.getLastModified();
            System.out.println(l2);
            byte[] CT2 = Long.toString(l2).getBytes();
            dataOutStream.write(CT2);
            dataOutStream.write(str.getBytes());

            //SEND CLIENT ID
            String s = tfcliID.getString();
            byte[] c = s.getBytes();
            dataOutStream.write(c);
            dataOutStream.write(str.getBytes());

            str = "/";
            dataOutStream.write(str.getBytes());

            //WRITE THE RECORD STORE DATA
            try {
                byte[] recData = new byte[5];
                int len;
                for (int i = 1; i <= rs.getNumRecords(); i++) {
                    if (rs.getRecordSize(i) > recData.length) {
                        recData = new byte[rs.getRecordSize(i)];
                    }
                    len = rs.getRecord(i, recData, 0);
                    str = new String(recData, 0, len);
                    byte postmsg[] = str.getBytes();
                    str = ",";
                    dataOutStream.write(postmsg);
                    dataOutStream.write(str.getBytes());
                }

            } catch (Exception e) {
                System.out.println("writing problem");
            }
            System.out.println("written");

            //READ FROM INPUT STREAM (OUTPUT OF SERVLET)
            DataInputStream In = http.openDataInputStream();
            int ch;
            while ((ch = In.read()) != -1) {
                sb.append((char) ch);
            }

            //PARSE SB AND STORE IN RECORDSTORE
            String sb1 = sb.toString();

            int i1 = sb1.indexOf(">");
            sb1 = sb1.substring(i1 + 1);

            int i = 1;
            while (i < 3) {
                int i2 = sb1.indexOf(",");

                ModifyRecord(sb1.substring(0, i2), Integer.toString(i));

                sb1 = sb1.substring(i2 + 1);
                i++;

            }
               System.out.println("before disy");
            

            //DISPLAY THAT IN A SERVLET
            TextField t = null;
            t = new TextField("Servlet" + s, sb.toString(), 1024, 0);
            Form outputForm = new Form("OUTPUT");
            outputForm.append(t);
            display.setCurrent(outputForm);

        } catch (Exception e) {
            System.out.println("error in servlet call");
        }

    }
}





