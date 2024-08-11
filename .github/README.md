# Flight Control  

![image](https://github.com/user-attachments/assets/94863847-75ef-448d-9cdf-8d076a55b4ff)

Stop players from hoarding all the elytras for themselves with **Flight Control**. With this lightweight plugin, you can control how many elytras a player can obtain from end cities in a configurable time period. This plugin specifically blocks obtaining elytras from end city ships; they will not trigger this restriction simply by throwing it on the ground and picking it up. To see how many elytras you can claim per time period, use the command `/flightcontrol`.

Example of `/flightcontrol`:  

![image](https://github.com/user-attachments/assets/bac177e6-cc57-49e4-843d-8fe5f44bcb4f)

Example config.yml:  
```
# The hours (of duration) in which you are limited to `max-count` elytras.
period-hours: 336

# The maximum number of elytras players can obtain within `period-hours`.
max-count: 2```
