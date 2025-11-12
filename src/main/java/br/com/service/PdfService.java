package br.com.service;

import br.com.api.dto.BombaDTO;
import br.com.api.dto.ProdutoDTO;
import br.com.pessoa.dto.PessoaResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class PdfService {

    private static final AtomicLong nfcCounter = new AtomicLong(1);

    public void gerarDanfeNfce(
            String operador,
            BombaDTO bomba,
            ProdutoDTO produto,
            double litros,
            double reais,
            PessoaResponse cliente,
            String formaPagamento
    ) throws IOException {
        long numeroNfce = nfcCounter.getAndIncrement();
        String chaveAcesso = gerarChaveAcesso();
        String dataHoraEmissao = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        DecimalFormat df = new DecimalFormat("#,##0.00");
        DecimalFormat dfLitros = new DecimalFormat("#,##0.000");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("DANFE-NFC-e - Documento Auxiliar da Nota Fiscal de Consumidor Eletrônica");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(50, 720);
                contentStream.showText("PDV Posto de Combustivel");
                contentStream.endText();

                contentStream.setStrokingColor(Color.BLACK);
                contentStream.setLineWidth(1);
                contentStream.moveTo(50, 710);
                contentStream.lineTo(550, 710);
                contentStream.stroke();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.newLineAtOffset(50, 690);
                contentStream.showText("Número da NFC-e: " + numeroNfce);
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Data e Hora da Emissão: " + dataHoraEmissao);
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Chave de Acesso: " + chaveAcesso);
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("PDV/Caixa: 01 - Operador: " + operador);
                contentStream.endText();

                contentStream.moveTo(50, 640);
                contentStream.lineTo(550, 640);
                contentStream.stroke();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(50, 620);
                contentStream.showText("Descrição do Produto");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.newLineAtOffset(50, 600);
                contentStream.showText(produto.getNome());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Quantidade (Volume): " + dfLitros.format(litros) + " L");
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Preço por Litro (Valor Unitário): R$ " + df.format(reais / litros));
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Valor Total do Item: R$ " + df.format(reais));
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Bomba: " + bomba.getNome());
                contentStream.endText();

                contentStream.moveTo(50, 530);
                contentStream.lineTo(550, 530);
                contentStream.stroke();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(50, 510);
                contentStream.showText("Forma de Pagamento");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.newLineAtOffset(50, 490);
                contentStream.showText(formaPagamento);
                contentStream.endText();

                contentStream.moveTo(50, 470);
                contentStream.lineTo(550, 470);
                contentStream.stroke();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(50, 450);
                contentStream.showText("Identificação do Consumidor");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.newLineAtOffset(50, 430);
                if (cliente != null) {
                    contentStream.showText("CPF/CNPJ: " + cliente.cpfCnpj());
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Nome: " + cliente.nomeCompleto());
                } else {
                    contentStream.showText("Consumidor Não Identificado");
                }
                contentStream.endText();
            }

            String fileName = "NFCe_" + numeroNfce + ".pdf";
            document.save(fileName);
            Desktop.getDesktop().open(new File(fileName));
        }
    }

    private String gerarChaveAcesso() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(44);
        for (int i = 0; i < 44; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
