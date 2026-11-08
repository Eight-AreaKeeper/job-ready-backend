package com.jobready.common.utils;

import com.jobready.pojo.entity.Resume;
import com.jobready.pojo.entity.User;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Renders resume sections ({ summary, skills[], education[], experience[], projects[] }) as a
 * simple one-column PDF. Uses the built-in Helvetica font, so text should be English/Latin.
 */
@Component
public class ResumePdfUtil {

    private static final Font NAME = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
    private static final Font CONTACT = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
    private static final Font HEADING = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(31, 78, 121));
    private static final Font BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font BODY = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font MUTED = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, Color.GRAY);

    public byte[] render(User user, Resume resume) {
        Map<String, Object> s = resume.getSections() == null ? Map.of() : resume.getSections();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.LETTER, 50, 50, 45, 45);
        PdfWriter.getInstance(doc, out);
        doc.addTitle(resume.getTitle());
        doc.open();

        Paragraph name = new Paragraph(user.getName(), NAME);
        name.setAlignment(Element.ALIGN_CENTER);
        doc.add(name);
        Paragraph contact = new Paragraph(user.getEmail(), CONTACT);
        contact.setAlignment(Element.ALIGN_CENTER);
        contact.setSpacingAfter(6);
        doc.add(contact);

        String summary = str(s.get("summary"));
        if (!summary.isBlank()) {
            heading(doc, "Summary");
            doc.add(new Paragraph(summary, BODY));
        }

        Collection<?> skills = list(s.get("skills"));
        if (!skills.isEmpty()) {
            heading(doc, "Skills");
            doc.add(new Paragraph(String.join(" | ", skills.stream().map(ResumePdfUtil::str).toList()), BODY));
        }

        Collection<?> experience = list(s.get("experience"));
        if (!experience.isEmpty()) {
            heading(doc, "Experience");
            for (Object o : experience) {
                Map<?, ?> e = map(o);
                entry(doc, join(" - ", str(e.get("title")), str(e.get("org"))),
                        join(" to ", str(e.get("start")), str(e.get("end"))));
                bullets(doc, list(e.get("bullets")));
            }
        }

        Collection<?> projects = list(s.get("projects"));
        if (!projects.isEmpty()) {
            heading(doc, "Projects");
            for (Object o : projects) {
                Map<?, ?> p = map(o);
                entry(doc, str(p.get("name")), "");
                bullets(doc, list(p.get("bullets")));
            }
        }

        Collection<?> education = list(s.get("education"));
        if (!education.isEmpty()) {
            heading(doc, "Education");
            for (Object o : education) {
                Map<?, ?> e = map(o);
                entry(doc, join(", ", str(e.get("degree")), str(e.get("school"))), str(e.get("end")));
            }
        }

        doc.close();
        return out.toByteArray();
    }

    private static void heading(Document doc, String text) {
        Paragraph p = new Paragraph(text.toUpperCase(), HEADING);
        p.setSpacingBefore(10);
        doc.add(p);
        LineSeparator line = new LineSeparator(0.5f, 100, new Color(31, 78, 121), Element.ALIGN_CENTER, 9);
        doc.add(new Chunk(line));
    }

    private static void entry(Document doc, String title, String dates) {
        Paragraph p = new Paragraph();
        p.setSpacingBefore(4);
        p.add(new Phrase(title, BOLD));
        if (!dates.isBlank()) {
            p.add(new Phrase("   " + dates, MUTED));
        }
        doc.add(p);
    }

    private static void bullets(Document doc, Collection<?> items) {
        if (items.isEmpty()) {
            return;
        }
        List list = new List(List.UNORDERED, 10);
        list.setListSymbol(new Chunk("•", BODY));
        for (Object b : items) {
            list.add(new ListItem(str(b), BODY));
        }
        doc.add(list);
    }

    private static String str(Object o) {
        return o == null ? "" : o.toString();
    }

    private static Collection<?> list(Object o) {
        return o instanceof Collection<?> c ? c : java.util.List.of();
    }

    private static Map<?, ?> map(Object o) {
        return o instanceof Map<?, ?> m ? m : Map.of();
    }

    private static String join(String sep, String a, String b) {
        if (a.isBlank()) {
            return b;
        }
        return b.isBlank() ? a : a + sep + b;
    }
}
