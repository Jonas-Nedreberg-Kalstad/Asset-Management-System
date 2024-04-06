package ntnu.group03.idata2900.ams.controllers;

import lombok.extern.slf4j.Slf4j;
import ntnu.group03.idata2900.ams.dto.CommentDto;
import ntnu.group03.idata2900.ams.model.Comment;
import ntnu.group03.idata2900.ams.services.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    private static final String COMMENT_NOT_FOUND = "Comment not found with id: {}";

    /**
     * Creates a new instance of CommentController.
     *
     * @param commentService commentService
     */
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Returns list of all comments in database
     *
     * @return List of all comments in database
     */
    @GetMapping
    public List<Comment> getAll() {
        return commentService.getAll();
    }

    /**
     * Get a comment from database matching given id if it exists.
     *
     * @param id potential id of a comments
     * @return a ModelAndView containing comments in JSON format
     */
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getComment(@PathVariable int id) {
        Optional<Comment> comment = this.commentService.getComment(id);
        if (comment.isEmpty()) {
            log.warn(COMMENT_NOT_FOUND, id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            log.info("Comment found with ID: {}", id);
            return new ResponseEntity<>(comment.get(), HttpStatus.OK);
        }
    }


    /**
     * Creates a new comment.
     *
     * @param comment The comment object to be created.
     * @return ResponseEntity containing the created comment and HTTP status code 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody CommentDto comment) {
        try {
            Comment createdComment = commentService.createComment(comment);
            log.info("Comment created with ID: {}", createdComment.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
        } catch (Exception e) {
            log.error("Error creating comment", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    /**
     * Updates an existing comment.
     *
     * @param id             The ID of the comment to be updated.
     * @param updatedComment The updated comment object.
     * @return ResponseEntity containing the updated comment (Optional) and HTTP status code 200 (OK) if successful,
     * or HTTP status code 404 (NOT_FOUND) if the comment with the given ID doesn't exist.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable int id, @RequestBody CommentDto updatedComment) {
        Optional<Comment> existingComment = commentService.getComment(id);
        if (existingComment.isEmpty()) {
            log.warn(COMMENT_NOT_FOUND, id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Comment commentToUpdate = existingComment.get();
            commentToUpdate.setComment(updatedComment.getComment());
            commentService.updateComment(commentToUpdate);
            log.info("Comment updated with ID: {}", id);
            return new ResponseEntity<>(commentToUpdate, HttpStatus.OK);
        }
    }

    /**
     * Deletes a comment.
     *
     * @param id The ID of the comment to be deleted.
     * @return ResponseEntity with HTTP status code 204 (NO_CONTENT) if successful,
     * or HTTP status code 404 (NOT_FOUND) if the comment with the given ID doesn't exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Comment> deleteComment(@PathVariable int id) {
        Optional<Comment> existingComment = commentService.getComment(id);
        if (existingComment.isEmpty()) {
            log.warn(COMMENT_NOT_FOUND, id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            commentService.deleteComment(id);
            log.info("Comment deleted with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}