package socialnetwork.domain;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import socialnetwork.utils.DateConstants;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class PDF {

    public PDF(List<Message> messageList, User user1, FriendDTO user2) throws FileNotFoundException, DocumentException {
        Document document = new Document();

        PdfWriter.getInstance(document, new FileOutputStream("messagesBetween" + user1.getLastName() + user2.getLastName() + ".pdf"));

        document.open();
        if (messageList.size() == 0) {
            String para = "No messages in list";
            Paragraph paragraph = new Paragraph (para);
            document.add(paragraph);
            document.close();
            return;
        }

        Field[] fieldsMessage = Message.class.getDeclaredFields();
        Object[] fieldsMess = fieldsToObjects(fieldsMessage);
        document.add(generateMessageTable(messageList));
        document.close();
    }


    private PdfPTable generateMessageTable(List<Message> messageList) {
        Field[] fieldsMessage = Message.class.getDeclaredFields();
        Object[] fieldsMess = fieldsToObjects(fieldsMessage);
        PdfPTable tableMessages = new PdfPTable(fieldsMess.length);
        tableMessages.setSpacingBefore(20F);
        addTableHeader(tableMessages, fieldsMess);
        messageList.forEach(x -> {
            tableMessages.addCell(x.getFrom().getLastName() + " " + x.getFrom().getFirstName());
            tableMessages.addCell(x.getTo().getName());
            tableMessages.addCell(x.getMessage());
            if(x.getReply() == null)
                tableMessages.addCell("null");
            else
                tableMessages.addCell(x.getReply().getMessage());
            tableMessages.addCell(x.getDate().format(DateConstants.DATE_TIME_FORMATTER));
        });
        return tableMessages;
    }


    private PdfPTable generateFriendsActivityTable(List<FriendDTO> friendDTOList) {
        Field[] fieldsFriendDTO = FriendDTO.class.getDeclaredFields();
        Object[] fieldsFriends = fieldsToObjects(fieldsFriendDTO);
        PdfPTable tableFriends = new PdfPTable(fieldsFriends.length);
        addTableHeader(tableFriends, fieldsFriends);
        friendDTOList.forEach(x -> {
            tableFriends.addCell(String.valueOf(x.getID()));
            tableFriends.addCell(x.getFirstName());
            tableFriends.addCell(x.getLastName());
            tableFriends.addCell(x.getDate().toString());
        });
        return tableFriends;
    }


    public PDF(List<FriendDTO> friendDTOList, List<Message> messageList) throws FileNotFoundException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("activities.pdf"));
        document.open();

        document.add(generateFriendsActivityTable(friendDTOList));

        if (messageList.size() == 0) {
            String para = "No messages in list";
            Paragraph paragraph = new Paragraph (para);
            document.add(paragraph);
        }
        else
            document.add(generateMessageTable(messageList));

        document.close();
    }


    private Object[] fieldsToObjects(Field[] fields) {
        return Arrays.stream(fields).map(x -> {
            String[] parts = x.toString().split("\\.");
            return parts[parts.length - 1];
        }).toArray();
    }

    private void addTableHeader(PdfPTable table, Object[] fields) {
        Stream.of(fields)
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(String.valueOf(columnTitle)));
                    table.addCell(header);
                });
    }

}
