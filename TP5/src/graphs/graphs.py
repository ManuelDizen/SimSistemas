import matplotlib.pyplot as plt
import numpy as np

colors = ['red', 'blue', 'green', 'orange', 'cyan',
          'purple', 'darkgreen', 'darkkhaki', 'coral', 'red']

def deriv(xvalues, yvalues):
    ydiff = np.diff(yvalues)
    xdiff = np.diff(xvalues)
    yprime = ydiff / xdiff
    xprime = []
    for i in range(len(yprime)):
        xtemp = (xvalues[i + 1] + xvalues[i]) / 2
        xprime = np.append(xprime, xtemp)
    return xprime, yprime


def time_to_leave(times, quantities):

    if not quantities:
        return None

    xtimes = []
    i = 1
    while i < len(times):
        diff = quantities[i-1] - quantities[i]
        while diff > 0:
            xtimes += [times[i]]
            diff-=1
        i+=1
    return xtimes

def calculate_average(arrays):
    if not arrays:
        return None

    array_lengths = [len(array) for array in arrays]
    min_length = min(array_lengths)

    average_array = []
    for i in range(min_length):
        sum_values = sum(array[i] for array in arrays)
        average = sum_values / len(arrays)
        average_array.append(average)

    return average_array

t_times = []
t_particles = []
times_to_leave = []

for i in range(1,10):
    file_name = '../../../punto_a_iter_{}.txt'.format(i)
    file = open(file_name,'r')
    lines = file.read().split('\n')
    times = []
    particles = []
    for line in lines:
        data = line.split(" ")
        if data[0] == '' or float(data[0]) > 80.0:
            continue
        times.append(float(data[0]))
        particles.append(float(data[1]))
    ttl = time_to_leave(times, particles)
    print(len(ttl))
    t_times.append(times)
    t_particles.append(particles)
    times_to_leave.append(ttl)

fig = plt.figure()
#for i in range(0,len(t_times)):
#    plt.plot(t_times[i], t_particles[i], color = colors[i])
array = (len(array) for array in t_times)
print(array)

avg = calculate_average(times_to_leave)
yvalues = range(1, 200)
plt.plot(avg, yvalues, color = 'blue')
plt.xlabel("Tiempo (s)")
plt.ylabel("Peatones evacuados")
plt.grid(visible=True)
plt.show()

xprime, yprime = deriv(avg, yvalues)

plt.plot(xprime, yprime, color = 'red')
plt.xlabel("Tiempo (s)")
plt.ylabel("Caudal")
plt.grid(visible=True)
plt.show()


