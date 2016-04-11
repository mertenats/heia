all: logo_sis.xpm logo_heia.xpm logo_i2c.xpm 

clean: 
	rm -f *.xpm *~

.PHONY: all clean

logo_sis.xpm: logo_sis.jpg	
	convert -resize 60% logo_sis.jpg logo_sis.xpm

logo_heia.xpm: logo_heia.jpg
	convert -resize 30% logo_heia.jpg logo_heia.xpm

logo_i2c.xpm: logo_i2c.png
	convert -resize 39% logo_i2c.png logo_i2c.xpm

