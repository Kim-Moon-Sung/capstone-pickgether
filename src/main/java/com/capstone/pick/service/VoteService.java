package com.capstone.pick.service;

import com.capstone.pick.domain.Hashtag;
import com.capstone.pick.domain.User;
import com.capstone.pick.domain.Vote;
import com.capstone.pick.domain.VoteHashtag;
import com.capstone.pick.domain.constant.SearchType;
import com.capstone.pick.dto.HashtagDto;
import com.capstone.pick.dto.VoteDto;
import com.capstone.pick.dto.VoteOptionDto;
import com.capstone.pick.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VoteService {

    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteHashtagRepository voteHashtagRepository;
    private final HashtagRepository hashtagRepository;

    @Transactional(readOnly = true)
    public List<VoteDto> findAllVotes() {
        List<Vote> votes = voteRepository.findAll();
        return votes.stream()
                .map(VoteDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VoteDto> searchVotes(SearchType searchType, String searchValue) {

        List<VoteDto> voteDtos = new ArrayList<>();

        switch (searchType) {
            case TITLE:
                voteDtos = voteRepository.findByTitleContaining(searchValue).stream().map(VoteDto::from).collect(Collectors.toList());
                break;
            case CONTENT:
                voteDtos = voteRepository.findByContentContaining(searchValue).stream().map(VoteDto::from).collect(Collectors.toList());
                break;
            case NICKNAME:
                voteDtos = voteRepository.findByUser_NicknameContaining(searchValue).stream().map(VoteDto::from).collect(Collectors.toList());
                break;
            case HASHTAG:
                List<Vote> votes = voteHashtagRepository.findByHashtag_ContentContaining(searchValue).stream()
                        .map(VoteHashtag::getVote).collect(Collectors.toList());
                voteDtos = votes.stream().map(VoteDto::from).collect(Collectors.toList());
                break;
        }

        return voteDtos;
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

    public void updateVote(Long voteId, VoteDto voteDto, List<VoteOptionDto> voteOptionDtos, List<HashtagDto> hashtagDtos) {
         try {
             Vote vote = voteRepository.getReferenceById(voteId);
             User user = userRepository.getReferenceById(voteDto.getUserDto().getUserId());

             if (vote.getUser().equals(user)) {
                 if (!voteDto.getTitle().equals(vote.getTitle())) {
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
                 if (!voteDto.getContent().equals(vote.getContent())) {
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
                 // TODO : ?????? ????????? ?????? ??????, ?????? ????????? ?????? ??? ?????? ?????? voteOption update ????????? ???????????????
             }
         } catch (EntityNotFoundException e) {
             log.warn("?????? ????????? ?????? ??????. ???????????? ??????????????? ????????? ????????? ?????? ??? ???????????? - {}", e.getLocalizedMessage());
         }
    }

    public void deleteVote(Long voteId, String userId) {
        voteRepository.deleteByIdAndUser_UserId(voteId, userId);
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
