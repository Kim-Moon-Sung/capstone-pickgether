package com.capstone.pick.service;

import com.capstone.pick.domain.Hashtag;
import com.capstone.pick.domain.User;
import com.capstone.pick.domain.Vote;
import com.capstone.pick.domain.VoteHashtag;
import com.capstone.pick.dto.HashtagDto;
import com.capstone.pick.dto.VoteDto;
import com.capstone.pick.dto.VoteOptionDto;
import com.capstone.pick.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteService {

    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteHashtagRepository voteHashtagRepository;
    private final HashtagRepository hashtagRepository;

    public List<VoteDto> findAllVotes() {
        List<Vote> votes = voteRepository.findAll();
        return votes.stream()
                .map(VoteDto::from)
                .collect(Collectors.toList());
    }

    public void saveVote(VoteDto dto, List<VoteOptionDto> voteOptionDtos, List<HashtagDto> hashtagDtos) {
        User user = userRepository.getReferenceById(dto.getUserDto().getUserId());
        Vote savedVote = voteRepository.save(dto.toEntity(user));
        voteOptionDtos.forEach(o -> voteOptionRepository.save(o.toEntity(savedVote)));
        List<Hashtag> savedHashtags = hashtagDtos.stream()
                .map(h -> hashtagRepository.save(h.toEntity()))
                .collect(Collectors.toList());

        savedHashtags.forEach(hashtag -> {
            voteHashtagRepository.save(
                    VoteHashtag.builder()
                            .vote(savedVote)
                            .hashtag(hashtag)
                            .build());
        });
    }

    public void updateVote(Long voteId, VoteDto voteDto, List<VoteOptionDto> voteOptionDtos, List<HashtagDto> hashtagDtos) throws Exception {
        Vote vote = voteRepository.getReferenceById(voteId);
        User user = userRepository.getReferenceById(voteDto.getUserDto().getUserId());

        if (vote.getUser().equals(user)) {
            if (voteDto.getTitle().equals(vote.getTitle())) {
                vote.changeTitle(voteDto.getTitle());
            }
            if (voteDto.getCategory() != vote.getCategory()) {
                vote.changeCategory(voteDto.getCategory());
            }
            if (voteDto.getExpiredAt() != vote.getExpiredAt()) {
                vote.changeExpiredAt(voteDto.getExpiredAt());
            }
            if (voteDto.isMultiPick() != vote.isMultiPick()) {
                vote.changeIsMultiPick(voteDto.isMultiPick());
            }
            if (voteDto.getDisplayRange() != vote.getDisplayRange()) {
                vote.changeDisplayRange(voteDto.getDisplayRange());
            }
            if (voteDto.getContent().equals(vote.getContent())) {
                vote.changeContent(voteDto.getContent());

                voteHashtagRepository.findAllByVoteId(voteId).forEach(vh -> hashtagRepository.delete(vh.getHashtag()));
                voteHashtagRepository.deleteAllByVoteId(voteId);

                List<Hashtag> savedHashtags = hashtagDtos.stream()
                        .map(h -> hashtagRepository.save(h.toEntity()))
                        .collect(Collectors.toList());
                savedHashtags.forEach(hashtag -> {
                    voteHashtagRepository.save(
                            VoteHashtag.builder()
                                    .vote(vote)
                                    .hashtag(hashtag)
                                    .build());
                });
            }
            // TODO : 투표 선택지 수정 여부, 수정 가능한 범위 등 상의 후에 voteOption update 작성할 예정입니다
        } else {
            throw new Exception("해당 게시글을 작성한 유저가 아닙니다.");
        }
    }

    public VoteDto getVote(Long voteId) {
        return VoteDto.from(voteRepository.getReferenceById(voteId));
    }

    public List<VoteOptionDto> getOptions(Long voteId) {
        return voteOptionRepository.findAllByVoteId(voteId).stream()
                .map(VoteOptionDto::from)
                .collect(Collectors.toList());
    }
}
