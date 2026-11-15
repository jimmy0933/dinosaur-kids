#!/usr/bin/env python3
"""Source-only checks: safe to run before secrets and release packaging exist."""
from pathlib import Path
import re
import sys
import xml.etree.ElementTree as ET
from PIL import Image

root=Path(__file__).resolve().parents[1]
res=root/'app/src/main/res'
for xml in res.rglob('*.xml'):
    ET.parse(xml)
ET.parse(root/'app/src/main/AndroidManifest.xml')
kt=(root/'app/src/main/java/com/jimmy/dinosaurkids/DinoData.kt').read_text()
ui=(root/'app/src/main/java/com/jimmy/dinosaurkids/MainActivity.kt').read_text()
ids=re.findall(r'Dino\("([a-z]+)"\s*,',kt)
assert len(ids)==18, ids
assert len(ids)==len(set(ids))
for id in ids:
    pic=res/'drawable-nodpi'/f'dino_{id}.png'
    assert pic.exists(), str(pic)
    with Image.open(pic) as img:
        img.verify()
    assert f'R.drawable.dino_{id}' in kt
for label in ['DexScreen','DetailScreen','ExploreScreen','FeedScreen','DigScreen','QuizScreen','AlbumScreen','SettingsScreen']:
    assert f'private fun {label}' in ui, label
manifest=(root/'app/src/main/AndroidManifest.xml').read_text()
assert 'android.permission.INTERNET' not in manifest
assert 'sensorLandscape' in manifest
wf=(root/'.github/workflows/release.yml').read_text()
for secret in ['KEYSTORE_BASE64','KEYSTORE_PASSWORD','KEY_ALIAS','KEY_PASSWORD']:
    assert secret in wf
assert 'gh release create latest' in wf
assert 'apksigner' in wf
print(f'PASS: XML parses; {len(ids)} unique images decode; all 8 screens present; no internet permission; signing gate and latest release configured.')