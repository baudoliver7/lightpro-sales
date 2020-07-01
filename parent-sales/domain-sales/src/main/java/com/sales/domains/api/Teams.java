package com.sales.domains.api;

import java.io.IOException;
import java.util.UUID;

import com.infrastructure.core.AdvancedQueryable;
import com.infrastructure.core.Updatable;

public interface Teams extends AdvancedQueryable<Team, UUID>, Updatable<Team> {
	Team add(String name, String shortName) throws IOException;
}
