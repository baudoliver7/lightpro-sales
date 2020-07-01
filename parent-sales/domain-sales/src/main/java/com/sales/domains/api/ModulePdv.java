package com.sales.domains.api;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.infrastructure.core.Nonable;
import com.securities.api.Company;

public interface ModulePdv extends Nonable {
	 UUID id();
	 String name() throws IOException;
	 Company company() throws IOException;
	 double turnover(LocalDate start, LocalDate end) throws IOException;
}
