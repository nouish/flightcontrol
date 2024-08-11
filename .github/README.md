Flight Control
==============

![image](https://github.com/user-attachments/assets/94863847-75ef-448d-9cdf-8d076a55b4ff)

[![GNU 3.0](https://img.shields.io/github/license/nouish/flightcontrol)](https://github.com/nouish/flightcontrol/blob/main/LICENSE)
[![GitHub Actions Status](https://img.shields.io/github/actions/workflow/status/nouish/flightcontrol/ci.yml)](https://github.com/nouish/flightcontrol/actions/workflows/ci.yml)
[![Version](https://img.shields.io/github/v/release/nouish/flightcontrol?sort=semver&display_name=tag)](https://github.com/nouish/flightcontrol/releases/latest)
[![GitHub Downloads](https://img.shields.io/github/downloads/nouish/flightcontrol/total)](https://github.com/nouish/flightcontrol/releases)
[![bStats Servers](https://img.shields.io/bstats/servers/22970)](https://bstats.org/plugin/bukkit/Elytra%20FlightControl/22970)

Stop players from hoarding all the elytras for themselves with **Flight Control**. With this lightweight plugin, you can control how many elytras a player can obtain from end cities in a configurable time period. This plugin specifically blocks obtaining elytras from end city ships; they will not trigger this restriction simply by throwing it on the ground and picking it up. To see how many elytras you can claim per time period, use the command `/flightcontrol`.

Example output from the `/flightcontrol` command:

![image](https://github.com/user-attachments/assets/bac177e6-cc57-49e4-843d-8fe5f44bcb4f)

Configuration
-------------

Example `plugins/FlightControl/config.yml`:

```yml
# The hours (of duration) in which you are limited to `max-count` elytras.
period-hours: 336

# The maximum number of elytras players can obtain within `period-hours`.
max-count: 2
```
