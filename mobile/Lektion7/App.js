import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View } from 'react-native';
import MapView, { Marker, PROVIDER_GOOGLE } from 'react-native-maps'
import {useState, useEffect} from 'react'

export default function App() {
  const [markers, setMarkers] = useState([])
  const region = {
    latitude:55.4,
    longitude:12,
    latitudeDelta:2,
    longitudeDelta:2
  }
  function handleLongPress(event){ // event har koordinaten indeni
      const {coordinate} = event.nativeEvent // hent kun coordinate objekt
      setMarkers(prev => [...prev, coordinate])
  }

  useEffect(()=>{
      alert("useEffect kaldes...")
      fetchAirQuality()
  },[])

  const key = "AIz..."
  async function fetchAirQuality() {
      // 
      try {
          const response = await fetch(
            "https://airquality.googleapis.com/v1/currentConditions:lookup?key=" + key,
            {
              method:"post",
              headers:{
                "Content-type":"application/JSON"
              },
              body: JSON.stringify({
                location:{
                  latitude:50,
                  longitude:-99
                }
              })
            }
          )
          const data = await response.json()
          const value = data.indexes[0].aqi
          alert("reply from Google " + JSON.stringify(data, null, 2))
      } catch (error) {
        alert("Error from Google " + JSON.stringify(error))
       }
  }

  return (
    <View style={styles.container}>
      <MapView
        provider={PROVIDER_GOOGLE}
        style={{width:'100%', height:'100%'}}
        region={region}
        onLongPress={handleLongPress}
      >
        { markers && markers.map((marker, index)=>(
            <Marker
              key={index}
              coordinate={marker}
              title="Go there"
              description="Good view"
            />
          ))
        }

      </MapView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
