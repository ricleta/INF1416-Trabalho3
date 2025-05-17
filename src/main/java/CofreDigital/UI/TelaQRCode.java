package CofreDigital.UI;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Dimension;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.image.BufferedImage;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Graphics;

public class TelaQRCode extends JFrame {
    private static final int QR_SIZE = 300;
    // Constructor
    public TelaQRCode(String base32TokenKey, String email) {
        setTitle("Tela de QR Code");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        String qrCodeContent = "otpauth://totp/Cofre%20Digital:" + email + "?secret=" + base32TokenKey;

        // Create the QR code panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JPanel qrCodePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    BufferedImage qrImage = generateQRCode(qrCodeContent);
                    g.drawImage(qrImage, 0, 0, null);
                } catch (WriterException e) {
                    e.printStackTrace();
                    g.setColor(Color.RED);
                    g.drawString("Error generating QR Code", 10, 20);
                }
            }
        };

        qrCodePanel.setPreferredSize(new java.awt.Dimension(QR_SIZE, QR_SIZE));
        qrCodePanel.setBackground(Color.WHITE);

        contentPanel.add(qrCodePanel);

        // Add base32TokenKey label
        JLabel base32TokenKeyLabel = new JLabel("Base32 Token Key: " + base32TokenKey);
        contentPanel.add(base32TokenKeyLabel);
        
        contentPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.add(contentPanel);
        pack();
    }    

    private BufferedImage generateQRCode(String content) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
        
        BufferedImage image = new BufferedImage(QR_SIZE, QR_SIZE, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < QR_SIZE; x++) {
            for (int y = 0; y < QR_SIZE; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return image;
    }
}
