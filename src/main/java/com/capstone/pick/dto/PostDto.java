package com.capstone.pick.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private VoteDto VoteDto;
    private List<VoteHashtagDto> voteHashtagDto;
}
