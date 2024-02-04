package io.certivity.backend.api;

import io.certivity.backend.model.Audit;
import io.certivity.backend.model.Page;
import io.certivity.backend.repository.AuditRepository;
import io.certivity.backend.repository.PageRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class ApiController {

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private AuditRepository auditRepository;

    public record CustomResponse(String message) {}

    @GetMapping("/")
    public ResponseEntity<CustomResponse> helloWorld() {
        return ResponseEntity.ok().body(new CustomResponse("Hello World!"));
    }

    @GetMapping("/getPageContent")
    public ResponseEntity<List<Page>> getPageContent(@RequestParam String url) {
        try {
            Document document = Jsoup.connect(url).get();
            List<Element> elements = document.select("p, h1, h2, h3, h4, h5, h6, ol, ul");
            int sortIndex = 0;
            for (Element element : elements) {
                String text = element.text();
                if (!text.isBlank()) {
                    Page existingPage = pageRepository.findByUrlAndSort(url, sortIndex);
                    if (existingPage == null) {
                        existingPage = new Page();
                    }
                    existingPage.setUrl(url);
                    existingPage.setHtml(element.outerHtml());
                    existingPage.setText(text);
                    existingPage.setLength(String.valueOf(text.length()));
                    existingPage.setSort(sortIndex++);
                    existingPage.setLastModified(new Date());
                    pageRepository.save(existingPage);
                }
            }

            List<Page> pages = pageRepository.findByUrlOrderBySortAsc(url);
            return ResponseEntity.ok().body(pages);

        } catch (IOException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/addComment/{id}")
    public ResponseEntity<String> addComment(@PathVariable String id,@RequestBody String comment) {
        Optional<Page> optional = pageRepository.findById(id);
        if(optional.isPresent()){
            Page page = optional.get();
            List<String> comments = page.getComments();
            if(comments == null) comments = new ArrayList<>();
            comments.add(comment);
            page.setComments(comments);
            pageRepository.save(page);
            auditRepository.save(new Audit(new Date(),"create","",comment));
            return ResponseEntity.ok().body("ok");

        }
        return ResponseEntity.ok().body("Something went wrong, please try again");
    }

    @PostMapping("/editComment/{id}")
    public ResponseEntity<String> editComment(@PathVariable String id,@RequestBody String comment) {
        //comment will be in form '[indexOfCommentEdited]:[comment]'
        String[] commentParts = comment.split(":", 2);
        int commentIndex = Integer.parseInt(commentParts[0]);
        String commentText = commentParts[1];

        Optional<Page> optional = pageRepository.findById(id);
        if(optional.isPresent()){
            Page page = optional.get();
            List<String> comments = page.getComments();
            String oldComment = comments.get(commentIndex);
            comments.set(commentIndex,commentText);
            page.setComments(comments);
            pageRepository.save(page);
            auditRepository.save(new Audit(new Date(),"edit",oldComment,commentText));
            return ResponseEntity.ok().body("ok");
        }
        return ResponseEntity.ok().body("Something went wrong, please try again");
    }

    @PostMapping("/deleteComment/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable String id,@RequestBody String comment) {
        //comment will be in form '[indexOfCommentEdited]:[comment]'
        Optional<Page> optional = pageRepository.findById(id);
        if(optional.isPresent()){
            Page page = optional.get();
            List<String> comments = page.getComments();
            comments.remove(comment);
            page.setComments(comments);
            pageRepository.save(page);
            auditRepository.save(new Audit(new Date(),"delete",comment,""));
            return ResponseEntity.ok().body("ok");
        }
        return ResponseEntity.ok().body("Something went wrong, please try again");
    }

    @GetMapping("/getAudits")
    public ResponseEntity<List<Audit>> getAudits() {
            return ResponseEntity.ok().body(auditRepository.findAll());

    }

    @GetMapping("/getPages")
    public ResponseEntity<List<Page>> getPages() {
        return ResponseEntity.ok().body(pageRepository.findAll());

    }

}

