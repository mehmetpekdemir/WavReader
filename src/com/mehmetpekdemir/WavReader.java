package com.mehmetpekdemir;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.logging.Logger;

public final class WavReader {

    private static final Logger LOGGER = Logger.getLogger(WavReader.class.getName());

    private static String hexString = "";

    private WavReader() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void readWavFile() {
        // Asking user for WAV file
        var dialog = new FileDialog((Frame) null, "Please choose a WAV file.");
        dialog.setMode(FileDialog.LOAD);
        dialog.setFile("*.wav;");
        dialog.setVisible(true);
        String fileDir = dialog.getDirectory();
        String file = dialog.getFile();
        read(dialog, fileDir, file);
    }

    // Helper function to reverse a string
    private static String reverseString(String s) {
        final var newString = new StringBuilder();
        for (var i = s.length() - 1; i > -1; i--) {
            newString.append(s.charAt(i));
        }
        return newString.toString();
    }

    // Helper function to obtain the little endian form of a string
    private static String littleEndian(String s) {
        final var newString = new StringBuilder();
        for (var i = 0; i <= s.length() - 1; i += 2) {
            newString.append(s.charAt(i + 1));
            newString.append(s.charAt(i));
        }
        return newString.toString();
    }

    // Helper function to convert a string in hex for decimal
    private static String hexToDec(String s) {
        return new BigInteger(s, 16).toString(10);
    }

    // Converts a hex value in little endian to decimal
    private static String convert(String convert) {
        convert = reverseString(convert);
        convert = littleEndian(convert);
        convert = hexToDec(convert);
        return convert;
    }

    // Helper function for read
    private static void read(FileDialog dialog, String fileDir, String file) {
        // Reading WAV File
        LOGGER.info("Reading WAV Data for file '" + file + "'. Please wait one moment.\n");
        try {
            var inFile = new BufferedInputStream(new FileInputStream(fileDir + file));
            var wavData = new ByteArrayOutputStream();

            int read;
            var buffer = new byte[1024];
            while ((read = inFile.read(buffer)) > 0) {
                wavData.write(buffer, 0, read);
            }
            wavData.flush();

            // Create a byte array to store the data then convert it to hex
            var byteData = wavData.toByteArray();
            var hexData = new String[byteData.length];

            for (var i = 0; i < byteData.length; i++) {
                var b = byteData[i];
                var s = String.format("%02x", b);
                hexData[i] = s;
            }

            for (String hexDatum : hexData) {
                WavReader.hexString += hexDatum;
            }
        } catch (Exception e) {
            LOGGER.info("Error! Please try again.");
            System.exit(1);
        }

        dialog.dispose();
        print(dialog.getFile());
    }

    // Helper function for print
    private static void print(String file) {
        // Printing file data
        System.out.println("Data for file '" + file + "' processed. Information provided below:\n");

        // Chunk Size
        var chunkSize = hexString.substring(8, 16);
        System.out.println("Chunk Size: " + convert(chunkSize));

        // SubChunk Size
        var subChunkSize = hexString.substring(32, 40);
        System.out.println("Subchunk Size: " + convert(subChunkSize));

        // Audio Format
        var audioFormat = hexString.substring(40, 44);
        System.out.println("Audio Format: " + convert(audioFormat));

        // Number of Channels
        var numChannels = hexString.substring(44, 48);
        System.out.println("Number of Channels: " + convert(numChannels));

        // Sampling Rate
        var samplingRate = hexString.substring(48, 56);
        System.out.println("Sampling Rate: " + convert(samplingRate));

        // Byte Rate
        var byteRate = hexString.substring(56, 64);
        System.out.println("Byte Rate: " + convert(byteRate));

        // Block Align
        var blockAlign = hexString.substring(64, 68);
        System.out.println("Block Align: " + convert(blockAlign));

        // Bits per Sample
        var bps = hexString.substring(68, 72);
        System.out.println("Bits per Sample: " + convert(bps));

        // SubChunk 2 Size
        var subChunk2Size = hexString.substring(80, 88);
        System.out.println("Subchunk 2 Size: " + convert(subChunk2Size));
    }

}