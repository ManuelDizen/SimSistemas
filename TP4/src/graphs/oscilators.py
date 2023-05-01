import numpy as np
import matplotlib.pyplot as plt
import math
import pandas as pd
import scipy as sp
from sklearn.neighbors import KernelDensity
from matplotlib import pyplot

def scatter():
    file = open("../output/beeman0.01.txt")
    readable = file.read().split('\n')[0:]
    print(str(len(readable)))
    i = 0
    for line in readable:
        readable[i] = line.split(' ')[0]
        i = i +1
    readable.remove('')
    values = np.array(readable).astype(float)
    print(values)

    times = np.arange(0, 5.01, 0.01)

    plt.scatter(times, values)
    plt.xlabel("Paso temporal (s)")
    plt.ylabel("Posición de partícula (m)")
    plt.grid(visible=True)
    plt.show()