package com.devsquad10.hub.presentation.res;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubDeleteResponseDto {
	private LocalDateTime deletedAt;
}
