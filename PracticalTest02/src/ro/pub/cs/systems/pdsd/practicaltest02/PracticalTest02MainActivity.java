package ro.pub.cs.systems.pdsd.practicaltest02;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import ro.pub.cs.systems.pdsd.practicaltest02.R;
import ro.pub.cs.systems.pdsd.practicaltest02.general.Constants;
import ro.pub.cs.systems.pdsd.practicaltest02.network.ClientThread;
import ro.pub.cs.systems.pdsd.practicaltest02.network.ServerThread;

public class PracticalTest02MainActivity extends Activity {
	
	private EditText serverPortEditText = null;
	private Button connectButton = null;

	// Client widgets
	private EditText clientAddressEditText = null;
	private EditText clientPortEditText = null;
	private EditText wordEditText = null;
	private Button getWordDefinitionButton = null;
	private TextView wordDefinitionTextView = null;

	private ServerThread serverThread = null;
	private ClientThread clientThread = null;
	
	private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(
                        getApplicationContext(),
                        "Server port should be filled!",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() != null) {
                serverThread.start();
            } else {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not creat server thread!");
            }

        }
    }
    
    private GetWordDefinitionButtonClickListener getWordDefinitionButtonClickListener = new GetWordDefinitionButtonClickListener();
    private class GetWordDefinitionButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort    = clientPortEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty() ||
                    clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(
                        getApplicationContext(),
                        "Client connection parameters should be filled!",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (serverThread == null || !serverThread.isAlive()) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] There is no server to connect to!");
                return;
            }

            String word = wordEditText.getText().toString();
            if (word == null || word.isEmpty()) {
                Toast.makeText(
                        getApplicationContext(),
                        "Parameters from client (word) should be filled!",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            wordDefinitionTextView.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread(
                    clientAddress,
                    Integer.parseInt(clientPort),
                    word,
                    wordDefinitionTextView);
            clientThread.start();
        }
    }
	
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
        connectButton = (Button)findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonClickListener);

        clientAddressEditText = (EditText)findViewById(R.id.client_address_edit_text);
        clientPortEditText = (EditText)findViewById(R.id.client_port_edit_text);
        wordEditText = (EditText)findViewById(R.id.word_edit_text);
//        informationTypeSpinner = (Spinner)findViewById(R.id.information_type_spinner);
        getWordDefinitionButton = (Button)findViewById(R.id.get_word_definition_button);
        getWordDefinitionButton.setOnClickListener(getWordDefinitionButtonClickListener);
        wordDefinitionTextView = (TextView)findViewById(R.id.word_text_view);
    }

    @Override
    protected void onDestroy() {
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.practical_test02_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
