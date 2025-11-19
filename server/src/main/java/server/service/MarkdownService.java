package server.service;

import commons.Note;
import commons.NoteTag;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import server.api.NoteController;
import server.api.TagController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service responsible for rendering Markdown content into HTML.
 * Uses the CommonMark library for parsing and rendering Markdown.
 */
@Service
public class MarkdownService {

    private final TagController tagController;
    private NoteController noteController;

    public MarkdownService(TagController tagController, @Lazy NoteController noteController) {
        this.tagController = tagController;
        this.noteController = noteController;
    }

    public void setNoteController(NoteController noteController) {
        this.noteController = noteController;
    }

    /**
     * Converts Markdown content into HTML.
     *
     * @param markdownContent The raw Markdown content to render.
     * @param note            the note rendered
     * @return The rendered HTML content.
     * @throws IllegalArgumentException if the input Markdown content is null or blank.
     */
    public String renderMarkdown(String markdownContent, Note note) {
        // Validate the input Markdown content.
        if (markdownContent == null) {
            throw new IllegalArgumentException("Markdown content cannot be null.");
        }
        if (markdownContent.isBlank()) {
            noteController.changeTags(note.getId(), new HashSet<>());
            return "";
        }
        markdownContent = findTags(note, markdownContent);
        markdownContent = findReferences(note, markdownContent);

        // Parse the Markdown content into a document node.
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownContent);

        // Render the document node into HTML and return the result.
        return HtmlRenderer.builder().build().render(document);
    }

    public String findReferences(Note note, String content) {
        Pattern pattern = Pattern.compile("\\[\\[(.*?)]]");
        Matcher matcher = pattern.matcher(content);

        List<Note> referencedNotes = new ArrayList<>();

        while (matcher.find()) {
            String chunk = matcher.group();
            String found = matcher.group(1);
            Note noteFound = noteController.getByTitle(found).getBody();

            if (noteFound == null) {
                content = content.replace(chunk, "<a style='color: gray; pointer-events: none; text-decoration: none;'>" + found + "</a>");
            } else {
                long id = noteFound.getId();
                content = content.replace(chunk, "<a style='color: blue; text-decoration: underline; cursor: pointer;' id=\"" + id + "\" onclick=\"window.noteApp.changeActiveNote('" + id + "')\">" + found + "</a>");
                referencedNotes.add(noteFound);
            }
        }

        noteController.setReferencedNotes(referencedNotes, note.getId());

        return content;
    }

    public String findTags(Note note, String content) {
        Pattern pattern = Pattern.compile("(?<!\\S)#\\w+");
        Matcher matcher = pattern.matcher(content);

        Set<NoteTag> tags = new HashSet<>();

        while (matcher.find()) {
            String found = matcher.group();
            NoteTag tag;
            if (tagController.getByName(found).isEmpty()) {
                tag = new NoteTag(found);
                tagController.add(tag);
            } else {
                tag = tagController.getByName(found).get();
            }
            tags.add(tag);
        }

        for (NoteTag tag : tags) {
            String sub = tag.getName().substring(1);
            content = content.replace(tag.getName(), "<button onclick=\"window.noteApp.buttonTagFilter('" + sub
                    + "')\">" + sub + "</button>");
        }
        noteController.changeTags(note.getId(), tags);

        return content;
    }

}