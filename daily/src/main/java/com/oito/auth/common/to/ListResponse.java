package com.oito.auth.common.to;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ListResponse<T> {

	private int itemsPerPage;
	private int page;
	private long totalItemsCount;

	List<T> rowData;
}
