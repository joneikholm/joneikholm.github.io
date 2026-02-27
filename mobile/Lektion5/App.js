import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View, Pressable, FlatList, TextInput, Modal, Image } from 'react-native';
import {useState, useEffect} from 'react'
import {collection, addDoc, doc, deleteDoc, updateDoc} from 'firebase/firestore'
// npm install react-firebase-hooks
import {useCollection} from 'react-firebase-hooks/firestore'
import {database, storage} from './firebase'
import * as ImagePicker from 'expo-image-picker'
import { ref, uploadBytes, getDownloadURL} from 'firebase/storage'


export default function App() {
const [text, setText] = useState("")
const [imagePath, setImagePath] = useState({})
const [imagePaths, setImagePaths] = useState({})
const [editObject, setEditObject] = useState({})
const [modalVisible, setModalVisible] = useState(false)
const [values, loading, error] = useCollection(collection(database, 'notes'))
const data = values?.docs.map((doc)=>({...doc.data(), id:doc.id})) ?? []


useEffect(()=>{
  // for hver note, hent billedet, hvis det findes
  data.forEach(doc =>{
    if(!imagePaths[doc.id]){
      downloadImage(doc.id, doc.id+".jpg")
    }
  })
},[data]) // skal justeres

if(loading) return <Text>Loading...</Text>
if (error) return <Text>Error: {String(error.message || error)}</Text>

async function addItem(){
  if(!text) return
  await addDoc(collection(database,'notes'),{
    text:text
  })
  setText("")
}

async function deleteItem(id){
  await deleteDoc(doc(database,'notes',id))
}

function updateItem(item){
  setEditObject(item)
  setText(item.text) 
  setModalVisible(!modalVisible)
}

async function saveUpdate(){
   updateDoc(doc(database,'notes',editObject.id),{
      text:text
   })
   uploadImage(editObject.id)
   setModalVisible(!modalVisible)
}

async function pickImage() {
  let result = await ImagePicker.launchImageLibraryAsync({
    allowsEditing:true
  })
  if(!result.canceled){
    setImagePath(result.assets[0].uri)
    console.log("billede OK")
  }
}

async function uploadImage(id){
    const res = await fetch(imagePath)
    const blob = await res.blob()
    const storageRef = ref(storage, id + ".jpg")
    uploadBytes(storageRef, blob).then((snap)=>{
      console.log("billede uploadet \n" + JSON.stringify(snap.metadata,null,2))
    })
}

async function downloadImage(id, fileName) {
  getDownloadURL(ref(storage, fileName)).
    then((url)=>{
      console.log("henter billede " + JSON.stringify(url,null,2))
      setImagePaths(paths => ({...paths, [id]: url}))
  })
}

  return (
    <View style={styles.container}>

      <Modal visible={modalVisible}
       onDismiss={()=> setText("")}
      >
      <View style={styles.modalContainer}>
        <Image source={{uri:imagePath}} style={{width:100, height:100}} />
        <Pressable onPress={pickImage} style={styles.editButtons}>
          <Text>Pick image</Text>
        </Pressable>
    
        <TextInput defaultValue={editObject.text} onChangeText={setText} />
        <Pressable onPress={saveUpdate}>
          <Text>Save</Text>
        </Pressable>
        <Pressable onPress={()=>setModalVisible(!modalVisible)}>
          <Text>Cancel</Text>
        </Pressable>
      </View>
      </Modal>
      <FlatList
        //style={styles.flatListStyle}
        data={data}
        keyExtractor={(x) => x.id}
        renderItem={({item})=> 
          <View style={styles.rowStyle}>  
            <Text>{item.text}</Text>
            <Image source={{uri: imagePaths[item.id]}} style={{width:100, height:100}}/>
            <Pressable style={styles.editButtons} onPress={()=>deleteItem(item.id)}>
              <Text>Delete</Text>
            </Pressable>
             <Pressable style={styles.editButtons} onPress={()=>updateItem(item)}>
              <Text>Update</Text>
            </Pressable>
          </View>
        }
        ListEmptyComponent={<Text>No notes yet.</Text>}
      />

    
     <Pressable onPress={downloadImage} style={styles.editButtons}>
      <Text>Download image</Text>
    </Pressable>

      <TextInput
        onChangeText={setText}
        placeholder='Type here'
        value={text}
      />
      <Pressable style={styles.addBtn}  onPress={addItem}>
        <Text>Add item</Text>
      </Pressable>
      <StatusBar style="auto" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    width: '100%',
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
    paddingTop:50,
    paddingBottom:50
  },
  flatListStyle:{
    //flex: 1
   // width: '100%' 
  },
  addBtn:{
    backgroundColor:'#ccc'
  }, 
  rowStyle:{
   flexDirection:'row',
   alignItems:'center',
   gap: 10,
   marginLeft: 10
  },
  editButtons:{
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderWidth: 1, 
    borderRadius: 8
  },
  modalContainer:{
    flex:1,
    marginTop:100,
    maxHeight:200,
    alignItems:'center',
    justifyContent:'center',
    backgroundColor:'orange'
  }
});
