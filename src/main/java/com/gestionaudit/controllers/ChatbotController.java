package com.gestionaudit.controllers;

import com.gestionaudit.utils.BadWordEnforcement;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import javafx.geometry.Pos;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ChatbotController {

    @FXML private ScrollPane scrollPane;
    @FXML private VBox chatHistory;
    @FXML private TextField messageField;
    @FXML private Button btnMic;

    private boolean isRecording = false;
    private TargetDataLine micLine;
    private File audioFile;

    private static final String API_KEY = "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";
    private static final String CHAT_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String WHISPER_URL = "https://api.groq.com/openai/v1/audio/transcriptions";
    private static final String CHAT_MODEL = "llama-3.3-70b-versatile";
    private static final String WHISPER_MODEL = "whisper-large-v3";

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(java.time.Duration.ofSeconds(15))
            .build();

    @FXML
    public void initialize() {
        // nothing to setup
    }

    @FXML
    private void toggleRecording() {
        if (!isRecording) {
            startRecording();
        } else {
            stopRecordingAndTranscribe();
        }
    }

    private void startRecording() {
        try {
            AudioFormat format = new AudioFormat(16000f, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            TargetDataLine selectedLine = null;
            for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
                Mixer mixer = AudioSystem.getMixer(mixerInfo);
                String name = mixerInfo.getName().toLowerCase();
                if (name.contains("stereo mix") || name.contains("what u hear") || name.contains("loopback")) {
                    continue;
                }
                if (mixer.isLineSupported(info)) {
                    try {
                        selectedLine = (TargetDataLine) mixer.getLine(info);
                        break;
                    } catch (LineUnavailableException ignored) {}
                }
            }

            if (selectedLine == null) {
                addMessageToChat("⚠️ Microphone non disponible.", false);
                return;
            }

            micLine = selectedLine;
            micLine.open(format);
            micLine.start();

            audioFile = File.createTempFile("groq_audio_", ".wav");
            isRecording = true;
            btnMic.setText("🛑");
            btnMic.setStyle("-fx-font-size: 18px; -fx-text-fill: #ef4444; -fx-border-color: #ef4444;");

            CompletableFuture.runAsync(() -> {
                try (AudioInputStream ais = new AudioInputStream(micLine)) {
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audioFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            addMessageToChat("⚠️ Erreur microphone : " + e.getMessage(), false);
        }
    }

    private void stopRecordingAndTranscribe() {
        if (micLine != null) {
            micLine.stop();
            micLine.close();
        }

        isRecording = false;
        btnMic.setText("🎤");
        btnMic.setStyle("-fx-font-size: 18px;");
        addMessageToChat("🎙️ Transcription...", false);

        CompletableFuture.supplyAsync(() -> transcribeWithWhisper(audioFile))
                .thenAccept(transcript -> {
                    javafx.application.Platform.runLater(() -> {
                        if (!chatHistory.getChildren().isEmpty()) {
                            chatHistory.getChildren().remove(chatHistory.getChildren().size() - 1);
                        }
                        if (transcript == null || transcript.isBlank()) {
                            addMessageToChat("⚠️ Aucun texte détecté.", false);
                            return;
                        }
                        messageField.setText(transcript);
                        handleSend();
                    });
                })
                .exceptionally(ex -> {
                    javafx.application.Platform.runLater(() ->
                            addMessageToChat("⚠️ Erreur : " + ex.getMessage(), false));
                    return null;
                });
    }

    private String transcribeWithWhisper(File wavFile) {
        try {
            String boundary = UUID.randomUUID().toString().replace("-", "");
            byte[] fileBytes = Files.readAllBytes(wavFile.toPath());
            ByteArrayOutputStream body = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(body, "UTF-8"), true);

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"model\"").append("\r\n\r\n");
            writer.append(WHISPER_MODEL).append("\r\n");
            writer.flush();

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"language\"").append("\r\n\r\n");
            writer.append("fr").append("\r\n");
            writer.flush();

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"audio.wav\"").append("\r\n");
            writer.append("Content-Type: audio/wav").append("\r\n\r\n");
            writer.flush();
            body.write(fileBytes);
            writer.append("\r\n");
            writer.append("--").append(boundary).append("--").append("\r\n");
            writer.flush();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(WHISPER_URL))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return new JSONObject(response.body()).getString("text").trim();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @FXML
    private void handleSend() {
        String msg = messageField.getText().trim();
        if (msg.isEmpty()) return;

        javafx.stage.Window win = messageField.getScene() != null ? messageField.getScene().getWindow() : null;
        if (BadWordEnforcement.blockIfViolating(msg, win)) return;

        addMessageToChat(msg, true);
        messageField.clear();

        callGroqAPI(msg).thenAccept(reply -> {
            javafx.application.Platform.runLater(() -> {
                addMessageToChat(reply, false);
                Notifications.create()
                        .title("IA Gestion Audit")
                        .text("Nouvelle réponse.")
                        .position(Pos.TOP_RIGHT)
                        .hideAfter(Duration.seconds(3))
                        .showInformation();
            });
        }).exceptionally(ex -> {
            javafx.application.Platform.runLater(() ->
                    addMessageToChat("Erreur technique.", false));
            return null;
        });
    }

    private void addMessageToChat(String content, boolean isUser) {
        HBox row = new HBox();
        row.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        VBox bubble = new VBox(5);
        bubble.getStyleClass().add(isUser ? "chat-bubble-client" : "chat-bubble-admin");
        bubble.setMaxWidth(450);
        Label textLabel = new Label(content);
        textLabel.setWrapText(true);
        textLabel.getStyleClass().add("chat-bubble-text");
        textLabel.setStyle(isUser ? "-fx-text-fill: white;" : "-fx-text-fill: black;");
        bubble.getChildren().add(textLabel);
        row.getChildren().add(bubble);
        chatHistory.getChildren().add(row);
        javafx.application.Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    private CompletableFuture<String> callGroqAPI(String prompt) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", CHAT_MODEL);
        JSONArray messages = new JSONArray();
        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", "Tu es un assistant pour l'application Gestion Audit.");
        messages.put(systemMsg);
        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        messages.put(userMsg);
        requestBody.put("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CHAT_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return new JSONObject(response.body())
                                .getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content").trim();
                    }
                    return "Erreur API.";
                });
    }
}
