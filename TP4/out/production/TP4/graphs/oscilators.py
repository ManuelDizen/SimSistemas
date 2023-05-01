import numpy as np

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

times = np.arange(0, 5, 0.01)
print(times)
print(str(values.size) + " " + str(times.size))