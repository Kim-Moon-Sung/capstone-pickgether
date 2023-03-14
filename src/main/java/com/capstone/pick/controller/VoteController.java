package com.capstone.pick.controller;

import com.capstone.pick.controller.form.VoteForm;
import com.capstone.pick.controller.form.VoteOptionFormListDto;
import com.capstone.pick.domain.constant.Category;
import com.capstone.pick.domain.constant.OrderCriteria;
import com.capstone.pick.dto.*;
import com.capstone.pick.security.VotePrincipal;
import com.capstone.pick.service.VoteCommentService;
import com.capstone.pick.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class VoteController {

    private final VoteService voteService;
    private final VoteCommentService voteCommentService;
    @GetMapping("/")
    public String home() {
        return "redirect:/timeline";
    }

    @GetMapping("/timeline")
    public String timeLine(@RequestParam(value = "category", required = false, defaultValue = "ALL") Category category,
                           @RequestParam(value = "orderBy", required = false, defaultValue = "LATEST") OrderCriteria orderBy,
                           Model model) {
        List<PostDto> posts = voteService.findAllVotesByCategoryAndOrderCriteria(category, orderBy);
        model.addAttribute("posts", posts);
        model.addAttribute("category", category);
        model.addAttribute("orderBy", orderBy);
        return "/page/timeLine";
    }

    @GetMapping("/form")
    public String createVote(Model model) {
        //TODO : 임시로 만든 폼 페이지는 의논 후 처리해야 함
        VoteForm voteForm = VoteForm.builder().build();
        //th:object 사용을 위해 폼 객체를 넘겨줌
        model.addAttribute("voteForm", voteForm);
        return "/page/form";
    }

    @PostMapping("/form")
    public String saveVote(@AuthenticationPrincipal VotePrincipal votePrincipal,
                           @ModelAttribute VoteForm voteForm,
                           @ModelAttribute(value = "VoteOptionFormListDto") VoteOptionFormListDto voteOptions) {
        VoteDto voteDto = voteForm.toDto(votePrincipal.toDto());
        List<HashtagDto> hashtagDtos = voteForm.getHashtagDtos();
        List<VoteOptionDto> voteOptionDtos = voteForm.getVoteOptions()
                .stream()
                .map(o -> o.toDto(voteDto))
                .collect(Collectors.toList());
        voteService.saveVote(voteDto, voteOptionDtos, hashtagDtos);
        return "redirect:/timeLine";
    }

    @GetMapping("/{voteId}/edit")
    public String editVote(@PathVariable("voteId") Long voteId, Model model) {
        VoteForm voteForm = VoteForm.builder().build();
        VoteDto voteDto = voteService.getVote(voteId);
        List<VoteOptionDto> optionDtos = voteService.getOptions(voteId);

        model.addAttribute("voteForm", voteForm);
        model.addAttribute("voteDto", voteDto);
        model.addAttribute("optionDtos", optionDtos);
        return "/page/editForm";
    }

    @PostMapping("/{voteId}/edit")
    public String editVote(@AuthenticationPrincipal VotePrincipal votePrincipal,
                           @PathVariable Long voteId, VoteForm voteForm) throws Exception {
        VoteDto voteDto = voteForm.toDto(votePrincipal.toDto());
        List<HashtagDto> hashtagDtos = voteForm.getHashtagDtos();
        List<VoteOptionDto> voteOptionDtos = voteForm.getVoteOptions()
                .stream()
                .map(o -> o.toDto(voteDto))
                .collect(Collectors.toList());
        voteService.updateVote(voteId, voteDto, voteOptionDtos, hashtagDtos);
        return "redirect:/timeLine"; // 투표 게시글 상세 페이지로 돌아간다던가
    }

    @PostMapping("/{voteId}/delete")
    public String deleteVote(
            @AuthenticationPrincipal VotePrincipal votePrincipal,
            @PathVariable Long voteId) {

        voteService.deleteVote(voteId, votePrincipal.getUsername());

        return "redirect:/timeline";
    }
}