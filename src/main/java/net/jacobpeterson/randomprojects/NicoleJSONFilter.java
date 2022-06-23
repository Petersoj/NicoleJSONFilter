package net.jacobpeterson.randomprojects;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NicoleJSONFilter {

    public static JTextArea foundArea;

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Nicole JSON Filter");

        JTextArea ptrackInputArea = new JTextArea("<ptrack1>\n<ptrack2>\n...\n");
        ptrackInputArea.setMinimumSize(new Dimension(100, 100));
        ptrackInputArea.setEditable(true);

        JButton analyzeButton = new JButton("Analyze");
        analyzeButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showOpenDialog(jFrame);
            if (fileChooser.getSelectedFile() != null) {
                try {
                    analyze(ptrackInputArea.getText(), fileChooser.getSelectedFile());
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }));

        foundArea = new JTextArea();

        jFrame.setLayout(new GridLayout(3, 1));
        jFrame.add(new JScrollPane(ptrackInputArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        jFrame.add(analyzeButton);
        jFrame.add(new JScrollPane(foundArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        jFrame.pack();
        jFrame.setSize(350, 300);
        jFrame.setMinimumSize(new Dimension(350, 300));
        jFrame.setLocationRelativeTo(null); // Centers the frame
        jFrame.setVisible(true);
    }

    public static void analyze(String textAreaText, File file) throws FileNotFoundException {
        String[] lines = textAreaText.split("\n");
        List<String> ptracksToFilter = new ArrayList<>(Arrays.asList(lines));

        StringBuilder stringBuilder = new StringBuilder("Found in IDs:\n");
        JsonReader jsonReader = new JsonReader(new FileReader(file));
        JsonObject object = new Gson().fromJson(jsonReader, JsonObject.class);
        for (JsonElement scheduleElement : object.getAsJsonArray("schedule")) {
            JsonObject scheduleObject = scheduleElement.getAsJsonObject();

            int ptrackExistCount = 0;
            for (JsonElement idRefs : scheduleObject.getAsJsonArray("category_id_refs")) {
                for (String filterPTrack : ptracksToFilter) {
                    if (idRefs.getAsJsonPrimitive().getAsString().equals(filterPTrack)) {
                        ptrackExistCount++;
                    }
                }
            }

            if (ptrackExistCount == ptracksToFilter.size()) {
                stringBuilder.append(scheduleObject.getAsJsonPrimitive("id").getAsString()).append("\n");
            }
        }

        foundArea.setText(stringBuilder.toString());
    }
}
