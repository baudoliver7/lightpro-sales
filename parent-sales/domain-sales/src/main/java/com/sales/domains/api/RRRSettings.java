package com.sales.domains.api;

import java.io.IOException;
import java.util.List;

public interface RRRSettings {
	RRRSetting setting(RRRType reductionType, ProductCategoryType exploitationProduct) throws IOException;
	List<RRRSetting> settings(RRRType reductionType) throws IOException;
}
