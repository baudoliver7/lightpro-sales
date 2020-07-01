package com.sales.reporting.report.api;

import java.io.IOException;

import com.infrastructure.core.Report;

public interface SalesReporting {
	Report quotation() throws IOException;
}
