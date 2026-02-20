import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View, TextInput, Pressable, FlatList, Modal } from 'react-native';
import {useState} from 'react'
import {collection, addDoc, deleteDoc, doc, updateDoc} from 'firebase/firestore'
import {database} from './firebase'
// npm install react-firebase-hooks
import {useCollection} from 'react-firebase-hooks/firestore'


export default function App() {
    const [theText, setText] = useState("")
    const [deleteID, setDeleteID] = useState("")
    const [editObject, setEditObject] = useState({})
    const [modalVisible, setModalVisible] = useState(false)
    const [values, loading, error] = useCollection(collection(database,'notes'))
    const data = values?.docs.map((doc) => ({...doc.data(), id:doc.id}))
  async function deleteItem(id){
    await deleteDoc(doc(database,'notes',id))
  }
  if (error) return <Text>Error:{String(error.message || error)}</Text>

  async function addItem(){
    await addDoc(collection(database,'notes'),{
      text:theText
    })
    setText("") // tøm feltet bagefter
    // Opgave: lav en delete, som tager dokument ID, og sletter dokumentet fra Firestore
  }
  
  function updateItem(item){
      setEditObject(item)
      setModalVisible(!modalVisible)
  }

  async function saveUpdate() {
    await updateDoc(doc(database,'notes',editObject.id),{
        text:theText
    })
    setModalVisible(!modalVisible)
  }

  return (
    <View style={styles.container}>
      <Modal visible={modalVisible} >
        <View>
          <TextInput defaultValue={editObject.text} onChangeText={setText}></TextInput>
          <Pressable onPress={saveUpdate}>
           <Text>Save</Text> 
          </Pressable>          
        </View>
      </Modal>

      <FlatList
        style={{flex:1, width:'100%', backgroundColor:'#eee'}}
        data={data}
        keyExtractor={(x) => x.id}
        renderItem={({item}) =>
            <View style={styles.rowstyle}>
            <Text>{item.text}</Text>
            <Pressable onPress={()=> deleteItem(item.id)}>
              <Text>Delete</Text>
            </Pressable>
            <Pressable onPress={()=> updateItem(item)}>
              <Text>Update</Text>
            </Pressable>
            </View>
        }
      />
      
      <TextInput value={theText} onChangeText={setText} />
      <Pressable onPress={addItem}>
        <Text>Save</Text>
      </Pressable>
       <TextInput value={deleteID} onChangeText={setDeleteID} />
      <Pressable onPress={deleteItem}>
        <Text>Delete</Text>
      </Pressable>
      <Text>Open up App.js to start working on your app!</Text>
      <StatusBar style="auto" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#ccc',
    alignItems: 'center',
    justifyContent: 'center',
  },
  rowstyle:{
    flexDirection:'row',
    alignItems:'center',
    marginLeft: 10,
    gap: 10
  }
});


// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import {getFirestore} from "firebase/firestore"
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "AIz4..",
  authDomain: "mdem",
  projectId: "md8b6",
  storageBucket: "mdem",
  messagingSenderId: "0116",
  appId: "1:38636794"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
export const database = getFirestore(app) // så kan vi bruge database direkte i koden.
