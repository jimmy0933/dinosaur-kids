#!/usr/bin/env python3
"""Generate original kid-friendly dinosaur illustrations with vector shapes.
No third-party dinosaur images or game assets are used.
Run: python tools/generate_dinosaur_art.py
Requires: cairosvg
"""
from pathlib import Path
import colorsys
import re
import cairosvg

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'app/src/main/res/drawable-nodpi'
OUT.mkdir(parents=True, exist_ok=True)

DINOS = [
 ('trex','theropod','#EF7854','#C84E45','tyrannosaurus'),
 ('triceratops','ceratops','#58B4A5','#258F84','tri'),
 ('stegosaurus','stego','#D6A164','#B97447','plates'),
 ('brachiosaurus','sauropod','#78B9DD','#4C91C2','longneck'),
 ('ankylosaurus','ankylo','#BFA3D6','#886FAE','armor'),
 ('velociraptor','theropod','#69BB8B','#3E9D71','feathers'),
 ('spinosaurus','theropod','#6D9FC3','#4D6DAD','sail'),
 ('parasaurolophus','hadro','#E0AC61','#BF7A46','crest'),
 ('allosaurus','theropod','#9AAED9','#667DBD','horns'),
 ('diplodocus','sauropod','#C69FCE','#9679B6','longtail'),
 ('pachycephalosaurus','theropod','#F1A2AF','#D36B88','dome'),
 ('therizinosaurus','theropod','#A9C96D','#7DA44A','claws'),
 ('carnotaurus','theropod','#DC8E6D','#BA5B46','bullhorns'),
 ('iguanodon','hadro','#83C9C2','#4A9D9D','thumb'),
 ('maiasaura','hadro','#DFB6D5','#BD82B4','mama'),
 ('oviraptor','theropod','#E4C077','#C49A49','beak'),
 ('dilophosaurus','theropod','#8FC7B1','#5CA48D','twincrests'),
 ('apatosaurus','sauropod','#9FC2A0','#6D9D79','heavy'),
]

def darken(hex_color, f):
    rgb = tuple(int(hex_color[i:i+2],16) for i in (1,3,5))
    return '#'+''.join(f'{max(0,min(255, int(c*f))):02X}' for c in rgb)

def ellipse(cx,cy,rx,ry, fill, more=''):
    return f'<ellipse cx="{cx}" cy="{cy}" rx="{rx}" ry="{ry}" fill="{fill}" {more}/>'

def path(d,fill, more=''):
    return f'<path d="{d}" fill="{fill}" {more}/>'

def circles(points, fill, opacity='.32'):
    return ''.join(ellipse(x,y,r,r,fill,f'opacity="{opacity}"') for x,y,r in points)

def eye(x,y, scale=1.0):
    return (ellipse(x,y,15*scale,17*scale,'#23383E')+
            ellipse(x-4*scale,y-5*scale,5*scale,5*scale,'#FFFFFF')+
            ellipse(x+5*scale,y+7*scale,2.5*scale,2.5*scale,'#7FB5B5'))

def mouth(x1,y1,x2,y2):
    return f'<path d="M {x1} {y1} Q {(x1+x2)/2} {y2+16} {x2} {y2}" fill="none" stroke="#5A544F" stroke-width="5" stroke-linecap="round"/>'

def feet(xs, y=344, fill='#4A7D6D'):
    s=''
    for x in xs:
        s += path(f'M{x-28} 284 Q{x-24} 324 {x-37} {y} Q{x-54} {y+11} {x-37} {y+17} L{x+31} {y+17} Q{x+45} {y+9} {x+23} {y-1} L{x+27} 285 Z',fill)
        for j in [-21,-3,15]:
            s+=path(f'M{x+j} {y+14} l4 9 l8 -9 Z','#FFF1D8', 'opacity=".95"')
    return s

def theropod(color, deep, feature):
    s = ''
    # Tail, limbs and round powerful body
    s += path('M 264 221 Q 176 208 42 151 Q 107 228 234 271 Q 278 285 304 261 Z',deep)
    s += path('M 282 257 Q 249 301 266 352 L 313 355 Q 318 349 303 339 L 313 296 Z',darken(deep,.86))
    s += ellipse(312,242,126,90,'url(#skin)')
    s += ellipse(345,268,76,50,'#FDE8B8','opacity=".23"')
    s += circles([(246,207,10),(280,193,13),(324,195,7),(264,249,7),(373,230,11)], '#FFFFFF', '.18')
    # Neck, head (distinct shapes)
    s += path('M 367 215 Q 399 139 458 148 L 488 189 Q 449 216 410 253 Z','url(#skin)')
    if feature=='dome':
        s += ellipse(470,148,66,50,deep)
        s += ellipse(467,134,59,38,'url(#skin)')
        s += ellipse(502,170,49,26,'url(#skin)')
        s += circles([(419,139,7),(431,112,7),(452,99,6),(479,98,7)],'#FFF4D7','.75')
    elif feature=='beak':
        s += path('M 427 135 Q 479 100 526 137 L 556 168 L 518 188 L 460 178 Z','url(#skin)')
        s += path('M 516 174 L 569 174 Q 547 204 515 190 Z','#EDBE63')
        s += path('M 440 132 Q 438 94 483 83 Q 471 115 485 135 Z',deep)
    else:
        s += path('M 419 142 Q 451 117 506 135 Q 523 148 551 158 Q 568 172 544 196 Q 515 204 469 191 Q 448 206 420 193 Z','url(#skin)')
        s += path('M 471 189 Q 519 203 545 191 Q 527 218 489 211 Z',deep,'opacity=".66"')
        if feature in ('tyrannosaurus','horns','bullhorns'):
            s += path('M 436 141 Q 430 121 439 109 Q 450 126 456 143 Z',deep)
            if feature in ('horns','bullhorns'):
                s += path('M 478 135 Q 480 93 496 96 Q 501 122 497 140 Z',deep)
        if feature=='twincrests':
            for x in (460,493):
                s += path(f'M{x-16} 139 Q{x-28} 84 {x-1} 86 Q{x+18} 93 {x+10} 139 Z','#E5A06D')
    if feature=='sail':
        s += path('M 216 205 Q 240 91 276 72 Q 289 107 309 78 Q 321 127 344 80 Q 367 122 381 205 Z','#EFA779')
        for i in range(5):
            x=245+i*28
            s += path(f'M{x} 191 L{x+11} {110+(i%2)*16}', 'none', 'stroke="#FFF3CC" stroke-width="4" opacity=".36"')
    if feature=='feathers':
        for x in (258,277,297,317,337,357):
            s += path(f'M{x} 167 l-13 -26 l30 22 Z','#D4DD8B')
        s += path('M 132 210 l-50 -48 l67 22 Z','#7EBC82')
    if feature=='claws':
        for x in (350,362,374):
            s += path(f'M{x} 244 Q{x+40} 271 {x+87} 247 Q{x+72} 282 {x+9} 273 Z','#FFF5D9')
    # leg in front
    s += path('M 346 276 Q 347 326 359 355 L 413 356 Q 417 348 399 342 L 394 288 Z',deep)
    for x in (374,391,407):
        s += path(f'M{x} 354 l5 10 l9 -10 Z','#FFF1D8')
    # arm and tiny hand
    s += path('M 407 230 Q 423 253 445 249 L 461 265 L 468 254 Q 449 232 429 219 Z',deep)
    s += path('M 463 264 l2 12 l7 -11 M 454 259 l-4 12 l8 -7','none','stroke="#FFF1D8" stroke-width="4" stroke-linecap="round"')
    s += eye(492 if feature!='dome' else 504,151 if feature!='dome' else 144)
    s += mouth(500,184,537,186)
    if feature=='tyrannosaurus':
        for x in (504,519,534): s += path(f'M{x} 191 l5 11 l5 -10 Z','#FFF7E4')
    if feature=='bullhorns':
        s += ellipse(522,179,7,5,'#9C604B','opacity=".52"')
    return s

def quadruped(color,deep,feature):
    s=path('M 219 231 Q 134 227 46 199 Q 115 252 211 278 Z',deep)
    s+=path('M 253 268 L 229 344 L 279 364 L 293 287 Z',darken(deep,.90))
    s+=path('M 371 266 L 360 351 L 408 358 L 423 265 Z',darken(deep,.9))
    s+=ellipse(306,248,171,90,'url(#skin)')
    s+=ellipse(302,273,132,50,'#FFE9C5','opacity=".20"')
    s+=circles([(193,223,13),(228,207,12),(280,195,9),(340,207,13),(374,240,9)],'#FFFFFF','.18')
    if feature=='plates':
        for i,(x,y,h) in enumerate([(195,177,58),(228,162,77),(265,150,86),(303,155,79),(341,169,64),(374,182,40)]):
            s+=path(f'M {x-21} {y+20} L{x} {y-h} L{x+26} {y+25} Z','#D78060')
            s+=path(f'M{x-12} {y+8} L{x} {y-h+15} L{x+11} {y+10} Z','#F7C18C','opacity=".75"')
        for x in (69,89,105,124):
            s+=path(f'M{x} 208 l-24 -21 l8 37 Z','#D78060')
    if feature=='armor':
        for x,y,r in [(197,214,19),(247,196,22),(302,195,20),(353,201,18),(395,224,19),(234,251,17),(285,243,18),(349,248,18)]:
            s+=path(f'M{x-r} {y+4} L{x-11} {y-15} L{x+7} {y-18} L{x+r} {y+7} Z','#DAD5A4')
        s+=path('M 76 205 Q 46 192 50 229 L72 244 L110 225 Z',deep)
        s+=ellipse(57,222,37,29,deep)
        s+=circles([(55,216,13)],'#F5DEB1','.50')
    if feature=='tri':
        s+=path('M 410 168 Q 412 99 463 112 Q 507 101 505 162 L 475 224 Z',deep)
        s+=path('M 423 157 Q 444 119 480 135 L 474 188 Z','#FAE9BC','opacity=".35"')
        s+=path('M 427 155 Q 416 93 433 93 Q 445 112 447 161 Z','#FFF4DA')
        s+=path('M 470 157 Q 486 92 497 105 Q 506 124 484 172 Z','#FFF4DA')
    s+=path('M 395 197 Q 436 177 491 203 Q 528 225 513 257 Q 486 267 461 244 Q 428 257 400 241 Z','url(#skin)')
    if feature=='tri':
        s+=path('M 499 213 Q 526 211 535 227 Q 527 241 506 232 Z','#FFF4DA')
    if feature=='plates':
        s+=ellipse(501,235,20,11,deep)
    s+=eye(474,209,0.8)
    s+=mouth(480,240,512,242)
    s+=path('M 226 267 Q 224 315 220 355 L 274 357 Q 286 343 273 338 L 281 278 Z',deep)
    s+=path('M 399 274 Q 393 319 403 357 L 449 357 Q 456 348 435 341 L 444 272 Z',deep)
    for x in (230,244,262,409,426,439):
        s+=path(f'M{x} 354 l5 10 l8 -10 Z','#FFF3DE')
    return s

def sauropod(color,deep,feature):
    s=path('M 250 231 Q 159 240 46 183 Q 110 265 250 287 Z',deep)
    if feature=='longtail':
        s=path('M 246 230 Q 131 242 29 164 Q 92 287 252 275 Z',deep)
    s+=path('M 260 273 Q 242 320 253 357 L 306 357 Q 305 345 284 340 L 308 287 Z',darken(deep,.94))
    s+=path('M 346 280 Q 332 325 347 357 L 400 357 Q 402 346 381 340 L 394 286 Z',darken(deep,.90))
    s+=ellipse(302,242,142,78,'url(#skin)')
    s+=ellipse(291,266,115,40,'#FAEFC3','opacity=".21"')
    s+=path('M 374 233 Q 410 197 400 144 Q 392 89 423 71 Q 454 63 458 98 Q 435 148 465 227 Q 450 248 413 256 Z','url(#skin)')
    if feature=='heavy':
        s+=path('M 415 122 Q 402 75 433 55 Q 460 52 464 91 L457 151 Z',deep)
    s+=ellipse(455,90,45,26,'url(#skin)')
    s+=ellipse(478,100,28,12,'url(#skin)')
    s+=eye(454,81,.72)
    s+=mouth(467,108,495,109)
    s+=path('M 222 266 Q 207 312 216 356 L 270 356 Q 279 345 256 337 L 272 279 Z',deep)
    s+=path('M 385 266 Q 379 317 391 359 L 441 359 Q 449 345 429 337 L 435 265 Z',deep)
    for x in (227,245,257,402,420,434):s+=path(f'M{x} 357 l4 8 l7 -8 Z','#FAEFDB')
    s+=circles([(240,211,12),(275,200,13),(315,198,10),(402,186,8),(414,151,7)],'#FFFFFF','.17')
    return s

def hadrosaur(color,deep,feature):
    s=path('M 253 222 Q 166 201 51 174 Q 111 242 251 276 Z',deep)
    s+=ellipse(304,245,136,83,'url(#skin)')
    s+=path('M 366 209 Q 395 158 436 155 Q 460 164 459 203 L409 251 Z','url(#skin)')
    if feature=='crest':
        s+=path('M 426 156 Q 394 122 384 76 Q 433 96 460 159 Z',deep)
        s+=path('M 420 150 Q 405 116 401 101 Q 435 127 442 153 Z','#FFDEAC','opacity=".50"')
    elif feature=='mama':
        s+=path('M 430 160 Q 442 130 458 131 L460 164 Z',deep)
    s+=path('M 432 159 Q 480 140 520 165 L554 186 Q 548 206 525 208 L464 199 Z','url(#skin)')
    s+=path('M 505 191 Q 540 196 553 190 Q 548 215 519 208 Z',deep)
    s+=eye(475,171,.85)
    s+=mouth(511,199,543,198)
    s+=path('M 230 277 L217 353 L264 359 L284 282 Z',deep)
    s+=path('M 364 274 Q 369 329 381 357 L429 359 Q 437 349 414 342 L412 266 Z',deep)
    for x in (227,247,260,391,413,425):s+=path(f'M{x} 356 l4 8 l7 -8 Z','#FFF2DF')
    s+=path('M 403 218 Q 425 248 445 248 L457 262 L466 255 Q 459 234 424 215 Z',deep)
    if feature=='thumb':s+=path('M 462 256 l18 -18 l-5 26 Z','#FFF5D5')
    s+=circles([(242,205,10),(265,196,10),(301,197,9),(357,220,10)],'#FFFFFF','.20')
    return s

TEMPLATES={'theropod':theropod,'ceratops':quadruped,'stego':quadruped,'ankylo':quadruped,'sauropod':sauropod,'hadro':hadrosaur}

def render_dino(spec):
    name,family,color,deep,feature=spec
    figure=TEMPLATES[family](color,deep,feature)
    svg=f'''<svg xmlns="http://www.w3.org/2000/svg" width="640" height="420" viewBox="0 0 640 420">
    <defs><linearGradient id="skin" x1="0" y1="0" x2=".8" y2="1"><stop stop-color="{color}"/><stop offset=".53" stop-color="{color}"/><stop offset="1" stop-color="{deep}"/></linearGradient>
    <filter id="blur"><feGaussianBlur stdDeviation="9"/></filter></defs>
    {ellipse(315,369,220,19,'#31534E','opacity=".18" filter="url(#blur)"')}
    <g stroke-linejoin="round" stroke-linecap="round">{figure}</g>
    </svg>'''
    cairosvg.svg2png(bytestring=svg.encode(),write_to=str(OUT/f'dino_{name}.png'),output_width=960,output_height=630)
    return len(svg)

def render_background():
    # Layered jungle vista in landscape. All custom vector artwork.
    s='''<svg xmlns="http://www.w3.org/2000/svg" width="1600" height="900" viewBox="0 0 1600 900"><defs>
      <linearGradient id="sky" x2=".2" y2="1"><stop stop-color="#B2E8DF"/><stop offset=".75" stop-color="#F5E7BA"/></linearGradient>
      <linearGradient id="land" x2="0" y2="1"><stop stop-color="#7ABD8B"/><stop offset="1" stop-color="#448B72"/></linearGradient>
      <linearGradient id="water" x2="0" y2="1"><stop stop-color="#A6DDD5"/><stop offset="1" stop-color="#5DB8B3"/></linearGradient>
    </defs>
    <rect width="1600" height="900" fill="url(#sky)"/>
    <circle cx="1320" cy="170" r="110" fill="#FFF4BF" opacity=".93"/>
    <path d="M0 504 Q200 360 365 454 Q588 283 822 436 Q1060 310 1279 440 Q1425 349 1600 420 L1600 900 L0 900 Z" fill="#A5CCAB"/>
    <path d="M0 573 Q247 397 445 538 Q711 418 966 550 Q1241 404 1600 537 L1600 900 L0 900Z" fill="#83B99A"/>
    <path d="M0 690 Q240 534 515 669 Q812 521 1035 672 Q1335 560 1600 654 L1600 900 L0 900Z" fill="url(#land)"/>
    <path d="M0 835 Q451 692 713 807 Q1009 900 1600 705 L1600 900 L0 900Z" fill="#3E8973"/>
    <path d="M530 900 Q610 746 885 730 Q1021 735 1146 900Z" fill="url(#water)" opacity=".96"/>
    <path d="M550 900 Q690 782 912 767 Q1028 774 1127 900" fill="none" stroke="#CFF4D4" stroke-width="12" opacity=".7"/>
    <g fill="#5E9E75" opacity=".7">'''
    for x,y,r in [(85,580,53),(160,598,66),(310,568,64),(1180,554,67),(1380,567,81),(1510,566,76),(1050,541,48)]:
        s+=ellipse(x,y,r,r,'#5E9E75')
    s+='''</g><g fill="#3E8776">'''
    for x,y,r in [(44,618,88),(167,654,63),(272,629,73),(1250,628,87),(1390,638,77),(1560,615,95)]:
        s+=ellipse(x,y,r,r,'#3E8776')
    s+='''</g><g fill="#295F5A">'''
    for x,y,scale in [(100,708,1),(230,731,.8),(1325,705,1.2),(1520,722,.9)]:
        s+=path(f'M{x} {y} l{-26*scale} {-83*scale} l{20*scale} {30*scale} l{9*scale} {-54*scale} l{14*scale} {51*scale} l{28*scale} {-29*scale} Z','#295F5A')
    s+='''</g><g fill="#FFFFFF" opacity=".57">'''
    for x,y in [(240,170),(500,100),(870,192)]:
        s+=ellipse(x,y,90,30,'#FFFFFF')+ellipse(x+55,y-15,65,36,'#FFFFFF')+ellipse(x-44,y-13,55,32,'#FFFFFF')
    s+='''</g></svg>'''
    cairosvg.svg2png(bytestring=s.encode(),write_to=str(OUT/'jungle_landscape.png'),output_width=1600,output_height=900)

if __name__=='__main__':
    for spec in DINOS:
        render_dino(spec)
    render_background()
    print(f'Generated {len(DINOS)} unique dinosaur illustrations + jungle landscape in {OUT}')