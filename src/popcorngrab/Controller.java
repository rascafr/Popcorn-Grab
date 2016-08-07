package popcorngrab;

import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by Rascafr, 06/08/2016
 * Dirty controller class, use with precaution
 */
public class Controller implements Initializable {

    private static final String BUTTER_PATH = "C:\\Users\\Rascafr\\AppData\\Local\\Temp\\Butter\\";
    private static final String D_PATH = "D:\\";
    private static final int BUTTER_LENGTH = 40;
    private static final String BUTTER_TORRENT = ".torrent";
    private HashMap<String, File> popFiles;

    @FXML
    private JFXListView<Label> listPop;

    @FXML
    private Label labelStatus;

    @FXML
    private void locateFile(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {

            // Get item / file
            if (listPop.getSelectionModel() != null && listPop.getSelectionModel().getSelectedItem() != null) {
                String name = listPop.getSelectionModel().getSelectedItem().getText();
                File fileIn = popFiles.get(name);

                // Ask for directory
                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setInitialDirectory(new File(D_PATH));
                chooser.setTitle("Choose directory");
                File fileOut = chooser.showDialog(((Node) event.getSource()).getScene().getWindow());

                // Check if cancel button haven't been clicked
                if (fileOut != null) {

                    // Copy directory / file using thread
                    new CopyThread(fileIn, fileOut).start();
                }
            }
        }
    }

    private class CopyThread extends Thread {

        private File fileIn, fileOut;
        private long t1;

        CopyThread(File fileIn, File fileOut) {
            this.fileIn = fileIn;
            this.fileOut = fileOut;

            labelStatus.setText("Copying " + fileIn.getName() + " ...");
        }

        @Override
        public void run() {
            t1 = System.currentTimeMillis();
            if (fileIn.isDirectory()) {
                try {
                    FileUtils.copyDirectoryToDirectory(fileIn, fileOut);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    FileUtils.copyFileToDirectory(fileIn, fileOut);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Platform.runLater(() -> {
                float timeSec = (System.currentTimeMillis()-t1)/1000.f;
                long fileLength = fileIn.isDirectory() ? folderSize(fileIn) : fileIn.length();
                String fileSize = sizeToString(fileLength);
                String fileWritingSpeed = sizeToString(fileLength / timeSec) + "/s";
                String fileWritingTime = String.format("%.2f", timeSec);
                String fileShortName = fileIn.getName().length() >= 20 ? fileIn.getName().substring(0, 19) + "..." : fileIn.getName();
                labelStatus.setText(fileShortName + " saved to " + fileOut.getPath() + " (" + fileSize + " in " + fileWritingTime + " sec, " + fileWritingSpeed + ")");
            });
        }
    }

    private static String sizeToString(float size) {
        if (size < 1024)
            return size + " bytes";
        else if (size < 1048576)
            return String.format("%.2f", size/1024.f) + " KB";
        else if (size < 1073741824)
            return String.format("%.2f", size/1048576.f) + " MB";
        else
            return String.format("%.2f", size/1073741824.f) + " GB";
    }

    private static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }

    /**
     * Called when the controller is initialized
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Images
        Image imgMovie = new Image(this.getClass().getResourceAsStream("res/Movie-24.png"));
        Image imgFolder = new Image(this.getClass().getResourceAsStream("res/Folder-24.png"));

        // Init array
        popFiles = new HashMap<>();

        // Loop on each directory / file
        File fileDir = new File(BUTTER_PATH);
        for (File fileEntry : fileDir.listFiles()) {

            // Each pop corn directory contains 40 chars
            if (fileEntry.isDirectory() && fileEntry.getName().length() == BUTTER_LENGTH) {

                // Get file or directory inside the directory
                // Could be movie (mkv, avi ...) or directory with movie and subtitle inside (YTS)
                for (File filePop : fileEntry.listFiles()) {

                    // Directory ? Or Single File, Check that this is not a torrent file
                    if (filePop.isDirectory() || filePop.getName().indexOf(BUTTER_TORRENT) != BUTTER_LENGTH) {
                        popFiles.put(filePop.getName(), filePop);
                        Label label = new Label(filePop.getName());
                        label.setGraphic(filePop.isDirectory() ? new ImageView(imgFolder) : new ImageView(imgMovie));
                        listPop.getItems().add(label);
                    }
                }
            }
        }

        // Show we user we are not tourists
        labelStatus.setText(popFiles.size() > 0 ? "Found " + popFiles.size() + " movie" + (popFiles.size() > 1 ? "s" : "") + " ! :D" : "No recently watched movies :(");
    }
}
