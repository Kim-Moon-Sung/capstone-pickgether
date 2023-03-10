package com.capstone.pick.service;

import com.capstone.pick.domain.User;
import com.capstone.pick.domain.Vote;
import com.capstone.pick.dto.CommentDto;
import com.capstone.pick.domain.VoteComment;
import com.capstone.pick.exeption.UserMismatchException;
import com.capstone.pick.repository.UserRepository;
import com.capstone.pick.repository.VoteCommentRepository;
import com.capstone.pick.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteCommentService {

    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final VoteCommentRepository voteCommentRepository;

    public List<CommentDto> readComment(Long voteId) {
        List<VoteComment> voteComments = voteCommentRepository.getVoteCommentsByVoteId(voteId);
        List<CommentDto> comments = voteComments
                .stream()
                .map(voteComment -> CommentDto.from(voteComment))
                .collect(Collectors.toList());
        return comments;
    }

    public void saveComment(CommentDto commentDto) {
        User user = userRepository.getReferenceById(commentDto.getUserDto().getUserId());
        Vote vote = voteRepository.getReferenceById(commentDto.getVoteId());
        voteCommentRepository.save(commentDto.toEntity(user, vote));
    }

    public void updateComment(Long commentId, CommentDto commentDto) throws UserMismatchException {
        VoteComment comment = voteCommentRepository.getReferenceById(commentId);
        User user = userRepository.getReferenceById(commentDto.getUserDto().getUserId());

        if(comment.getUser().equals(user)) {
            if(commentDto.getContent() != null) {
                comment.changeContent(commentDto.getContent());
            }
        } else {
            throw new UserMismatchException();
        }
    }

    public void deleteComment(Long commentId, String userId) throws UserMismatchException {
        User user = userRepository.getReferenceById(userId);
        VoteComment voteComment = voteCommentRepository.getReferenceById(commentId);

        if(voteComment.getUser().equals(user)) {
            voteCommentRepository.delete(voteComment);
        } else {
            throw new UserMismatchException();
        }
    }
}
