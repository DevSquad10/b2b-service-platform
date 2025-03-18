package com.devsquad10.hub.application.service;

import java.util.Collections;

import org.springframework.stereotype.Service;

import com.devsquad10.hub.application.dto.RouteCalculationResult;
import com.devsquad10.hub.domain.model.Hub;

@Service
public class P2PRouteCalculateService implements HubRouteCalculateService {
	@Override
	public RouteCalculationResult calculateRoute(Hub departureHub, Hub destinationHub) {

		// 하버사인 공식을 이용한 거리 계산(미터 단위)
		double distance = calculateHaversineDistance(
			departureHub.getLatitude(), departureHub.getLongitude(),
			destinationHub.getLatitude(), destinationHub.getLongitude()
		);

		// 평균 50km/h로 이동 한다 가정 할 경우 예상 시간(밀리 초)
		int durationInMillis = (int)(distance / 50.0 * 3600);

		return RouteCalculationResult.builder()
			.distance(distance)
			.duration(durationInMillis)
			.waypoints(Collections.emptyList())
			.build();
	}

	/**
	 * 하버사인 공식을 사용한 두 지점 간 거리 계산
	 * @param lat1 위도1
	 * @param lon1 경도1
	 * @param lat2 위도2
	 * @param lon2 경도2
	 * @return 두 좌표의 거리 (미터 단위)
	 */
	private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
		final double R = 6378.137;

		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
			Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return R * c * 1000;
	}
}
