import matplotlib.pyplot as plt

colors = ['red', 'blue', 'green', 'orange', 'cyan',
          'purple', 'darkgreen', 'darkkhaki', 'coral', 'red']

t_times = []
t_particles = []

for i in range(0,10):
    file_name = '../../punto_a_iter_{}.txt'.format(i)
    file = open(file_name,'r')
    lines = file.read().split('\n')
    times = []
    particles = []
    for line in lines:
        data = line.split(" ")
        if data[0] == '' or float(data[0]) > 65.0:
            continue
        times.append(float(data[0]))
        particles.append(float(data[1]))
    t_times.append(times)
    t_particles.append(particles)

fig = plt.figure()
for i in range(0,len(t_times)):
    plt.plot(t_times[i], t_particles[i], color = colors[i])
plt.xlabel("Tiempo (s)")
plt.ylabel("Partículas en recinto")
plt.grid(visible=True)
plt.show()