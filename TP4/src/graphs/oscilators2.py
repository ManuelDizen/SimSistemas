import numpy as np
import matplotlib.pyplot as plt
import math

beeman_values = [4.484448e-05, 2.880218e-07, 2.750178e-09, 2.737481e-11]
verlet_values = [5.725727e-04, 4.898959e-06, 4.821672e-08, 4.813985e-10]
gear_values = [4.048045e-12, 2.999216e-22, 1.436273e-24, 5.572282e-23]

fig = plt.figure()
#ax = fig.add_subplot(2,1,1)
precisions = ["10^-2", "10^-3", "10^-4", "10^-5"]
plt.plot(precisions, beeman_values, label="beeman", linestyle="solid", color="k")
plt.plot(precisions, verlet_values, label="verlet", linestyle="solid", color="r")
plt.plot(precisions, gear_values, label="gear", linestyle="solid", color="m")
#ax.set_yscale('log')
plt.yscale("log")
plt.xlabel("Paso temporal Δt (s)")
plt.legend( loc='upper right')
plt.ylabel("Error Cuadrático Medio")
plt.grid(visible=True)
plt.show()