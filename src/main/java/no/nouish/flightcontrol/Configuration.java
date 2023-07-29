/*
 * This file is part of FlightControl, a plugin for Spigot servers.
 *
 * Copyright (C) 2023  Erik Eide
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package no.nouish.flightcontrol;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

final class Configuration
{
  private final FlightControl flightControl;

  Configuration(FlightControl flightControl)
  {
    this.flightControl = Objects.requireNonNull(flightControl);
  }

  public int getMaxCount()
  {
    return flightControl.getConfig().getInt("max-count", 2);
  }

  public long getPeriodHours()
  {
    return flightControl.getConfig().getLong("period-hours", TimeUnit.DAYS.toHours(7));
  }
}
